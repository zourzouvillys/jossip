package io.rtcore.gateway.client.proxy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.UnicastSubject;
import io.rtcore.gateway.api.SipResponsePayload;
import io.rtcore.gateway.client.SipGateway.ClientInviteDelegate;
import io.rtcore.sip.common.iana.SipStatusCategory;
import io.rtcore.sip.common.iana.SipStatusCodes;

public class ProxyContext {

  private static final Logger log = LoggerFactory.getLogger(ProxyContext.class);

  private enum State {

    // we're currently trying to find a branch. new ones which come in will be sent and we move to
    // WAITING once out, SUCCEEDED once we have a 2xx one, or FAILED once all are done without
    // success.
    BRANCHING,

    // we are out of targets, waiting for any downstreams to complete.
    WAITING,

    // at least one branch has returned a 2xx, we're cleaning up now. no more targets accepted.
    SUCCEEDED,

    // all branches have failed, no more targets. done.
    FAILED,

  }

  private enum CompletionReason {

    // no more targets to send.
    NO_MORE_TARGETS,

    // error getting targets
    ERROR_FETCHING_TARGETS,

  }

  private Disposable targetSubscription;
  private final ClientInviteDelegate delegate;
  private final Maybe<ProxyCancelReason> cancellatonToken;
  private final Set<BranchContext> branches = new HashSet<>();
  // branches which are completed but not finished
  private final Set<BranchContext> completed = new HashSet<>();
  private final List<SipResponsePayload> responses = new ArrayList<>();
  private State state = State.BRANCHING;

  public static Observable<SipResponsePayload> proxy(final Flowable<ProxyBranchTarget> targets, final Maybe<ProxyCancelReason> cancellatonToken) {
    return Observable.defer(() -> {
      final UnicastSubject<SipResponsePayload> res = UnicastSubject.create();
      new ProxyContext(new DelegateForwarder(res), targets, cancellatonToken);
      return res;
    });
  }

  public static void proxy(final ClientInviteDelegate delegate, final Flowable<ProxyBranchTarget> targets, final Maybe<ProxyCancelReason> cancellatonToken) {
    new ProxyContext(delegate, targets, cancellatonToken);
  }

  private static class DelegateForwarder implements ClientInviteDelegate {

    private final Observer<SipResponsePayload> subject;

    public DelegateForwarder(final Observer<SipResponsePayload> subject) {
      this.subject = subject;
    }

    @Override
    public void onNext(final SipResponsePayload res) {
      this.subject.onNext(res);
    }

    @Override
    public void onError(final Throwable t) {
      this.subject.onError(t);
    }

    @Override
    public void onComplete() {
      this.subject.onComplete();
    }

  }

  private ProxyContext(final ClientInviteDelegate delegate, final Flowable<ProxyBranchTarget> targets, final Maybe<ProxyCancelReason> cancellatonToken) {

    //
    this.delegate = delegate;

    // token which triggers when cancelled.
    this.cancellatonToken = cancellatonToken;

    // subscribe to the targets
    this.targetSubscription =
      targets
        .takeUntil(cancellatonToken.toFlowable())
        .subscribe(
          this::handleNewTarget,
          err -> this.handleNoMoreTargets(Optional.of(err)),
          () -> this.handleNoMoreTargets(Optional.empty()));

  }

  private boolean hasSentFinalUpstream() {
    return switch (this.state) {
      case BRANCHING, WAITING -> false;
      case FAILED, SUCCEEDED -> true;
    };
  }

  private class BranchContext implements ClientInviteDelegate {

    SipResponsePayload finalResponse = null;

    public BranchContext() {
    }

    @Override
    public void onNext(final SipResponsePayload res) {

      log.info("downstream branch: {}", res.statusCode());

      switch (SipStatusCategory.forCode(res.statusCode())) {

        case TRYING:
          break;

        case PROVISIONAL:
          // send upstream
          if (!ProxyContext.this.hasSentFinalUpstream()) {
            ProxyContext.this.delegate.onNext(res);
          }
          break;

        case SUCCESSFUL:

          // store the final response.
          this.finalResponse = res;
          ProxyContext.this.completed.add(this);
          // 2xx always gets sent upstream.
          ProxyContext.this.state = State.SUCCEEDED;
          ProxyContext.this.delegate.onNext(res);
          break;

        case REDIRECTION:
        case REQUEST_FAILURE:
        case SERVER_FAILURE:
        case GLOBAL_FAILURE:
          //
          this.finalResponse = res;
          // remove the branch as it's completed.
          ProxyContext.this.branches.remove(this);
          // what to do?
          switch (ProxyContext.this.state) {
            case WAITING:
              ProxyContext.this.responses.add(res);
              // nothing to do, wait for target selector.
              break;
            case BRANCHING:
              ProxyContext.this.responses.add(res);
              ProxyContext.this.trySendFailure(CompletionReason.NO_MORE_TARGETS);
              break;
            case SUCCEEDED:
              // nothing to do, ignore the failure.
              break;
            case FAILED:
              throw new IllegalStateException();
          }
          break;

      }

    }

    @Override
    public void onError(final Throwable t) {
      log.info("got branch error: {}", t.toString());
      this.finish(Optional.of(t));
    }

    @Override
    public void onComplete() {
      this.finish(Optional.empty());
    }

    private void finish(final Optional<Throwable> error) {

      ProxyContext.this.branches.remove(this);

      log.info("removed branch after completion {}, now {}/{}",
        error,
        ProxyContext.this.branches.size(),
        ProxyContext.this.completed.size());

      switch (ProxyContext.this.state) {
        case BRANCHING:
          // nothing to do, other targets possible.
          break;
        case WAITING:
          ProxyContext.this.trySendFailure(CompletionReason.NO_MORE_TARGETS);
          break;
        case FAILED:
          break;
        case SUCCEEDED:
          // nothing to do.
          if (ProxyContext.this.completed.remove(this) && ProxyContext.this.completed.isEmpty()) {
            // completed after success
            ProxyContext.this.delegate.onComplete();
          }
          break;
      }

    }

  }

  /**
   * a new target has been provided, which we will forward a new branch to.
   */

  private void handleNewTarget(final ProxyBranchTarget target) {

    log.info("sending to {}", target);

    switch (this.state) {
      case BRANCHING:
        break;
      case WAITING:
        // weird, shouldn't happen as only move to this state once out of targets.
        log.warn("unable to add new target while in state WAITING");
        throw new IllegalStateException();
      case SUCCEEDED:
        // if we have succeeded we don't try to generate any more.
        log.warn("skipping new target while in state SUCCEEDED: {}", target);
        return;
      case FAILED:
        log.warn("skipping new target while in state FAILED");
        return;
    }

    final BranchContext ctx = new BranchContext();
    this.branches.add(ctx);

    try {
      target.send(ctx, this.cancellatonToken);
    }
    catch (final Exception ex) {
      ctx.onError(ex);
    }

  }

  /**
   * called when there are no more targets to be tried, either because of an error or just run out.
   */

  private void handleNoMoreTargets(final Optional<Throwable> error) {

    log.info("noMoreTargets({}): {} ({} branches)", error, this.state, this.branches.size());

    // we clear the subscription as it can't be terminated once terminated ...
    this.targetSubscription = null;

    //
    switch (this.state) {

      case BRANCHING:
        this.state = State.WAITING;
        // no branches and no more will happen. send final failure.
        this.trySendFailure(error.map(e -> CompletionReason.ERROR_FETCHING_TARGETS).orElse(CompletionReason.NO_MORE_TARGETS));
        break;

      case WAITING:
        // weird, shouldn't happen as only move to this state once out of targets.
        log.warn("got no more targets while in state {}", this.state);
        throw new IllegalStateException();

      case FAILED:
        // we shouldn't ever send a failure until no more targets left.
        log.warn("got no more targets while in state {}", this.state);
        throw new IllegalStateException();

      case SUCCEEDED:
        log.warn("no more targets while in state SUCCEEDED");
        // no more active branches so we complete upstream too.
        break;

    }

  }

  private void trySendFailure(final CompletionReason reason) {
    log.info("trying failure send while state {}, {} failures", this.state, this.responses.size());
    switch (this.state) {
      case BRANCHING:
        return;
      case WAITING:
        if (!this.branches.isEmpty()) {
          // stil have more branches to go.
          log.debug("WAITING for branches to complete");
          return;
        }
        log.debug("{} -> FAILED", this.state);
        this.state = State.FAILED;
        try {
          this.delegate.onNext(this.calculateFailure(reason));
          this.delegate.onComplete();
        }
        catch (final Exception ex) {
          log.info("Sending error: {}", ex.toString());
          this.delegate.onError(ex);
        }
        break;
      case FAILED:
      case SUCCEEDED:
        throw new IllegalArgumentException();
    }

  }

  private SipResponsePayload calculateFailure(final CompletionReason reason) {
    log.info("generating response for {}", reason);
    if (this.responses.isEmpty()) {
      switch (reason) {
        case ERROR_FETCHING_TARGETS:
          throw new SipProxyException(SipStatusCodes.SERVER_INTERNAL_ERROR);
        case NO_MORE_TARGETS:
          throw new SipProxyException(SipStatusCodes.TEMPORARILY_UNAVAILABLE);
      }
      throw new SipProxyException(SipStatusCodes.SERVER_INTERNAL_ERROR);
    }
    throw new RuntimeException("no responses available for error: " + reason);
  }

}
