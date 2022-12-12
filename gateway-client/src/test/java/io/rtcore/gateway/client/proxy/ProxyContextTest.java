package io.rtcore.gateway.client.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.subjects.MaybeSubject;
import io.rtcore.gateway.api.ImmutableSipResponsePayload;
import io.rtcore.gateway.api.SipResponsePayload;
import io.rtcore.gateway.client.SipGateway.ClientInviteDelegate;
import io.rtcore.sip.common.iana.SipStatusCodes;

class ProxyContextTest {

  static class UpstreamLogger implements ClientInviteDelegate {

    @Override
    public void onNext(final SipResponsePayload res) {
      System.err.println(">>> " + res);
    }

    @Override
    public void onError(final Throwable t) {
      System.err.println(">>> " + t.getMessage());
    }

    @Override
    public void onComplete() {
      System.err.println(">>> COMPLETE");
    }

  }

  class ImmediateRejection implements ProxyBranchTarget {

    private final SipStatusCodes code;

    public ImmediateRejection(final SipStatusCodes code) {
      this.code = code;
    }

    @Override
    public void send(final ClientInviteDelegate delegate, final Maybe<ProxyCancelReason> cancellationToken) {
      delegate.onNext(
        ImmutableSipResponsePayload.builder()
          .statusCode(this.code.statusCode())
          .build());
      delegate.onComplete();
    }

  }

  class Success implements ProxyBranchTarget {

    private final Completable completion;

    public Success(final Completable completion) {
      this.completion = completion;
    }

    @Override
    public void send(final ClientInviteDelegate delegate, final Maybe<ProxyCancelReason> cancellationToken) {
      delegate.onNext(
        ImmutableSipResponsePayload.builder()
          .statusCode(200)
          .build());
      this.completion.subscribe(() -> {
        delegate.onComplete();
      });
    }

  }

  class PendingOnly implements ProxyBranchTarget {

    private final SipStatusCodes code;

    public PendingOnly(final SipStatusCodes code) {
      this.code = code;
    }

    @Override
    public void send(final ClientInviteDelegate delegate, final Maybe<ProxyCancelReason> cancellationToken) {
      delegate.onNext(
        ImmutableSipResponsePayload.builder()
          .statusCode(this.code.statusCode())
          .build());

      cancellationToken.subscribe(cancel -> {
        delegate.onNext(
          ImmutableSipResponsePayload.builder()
            .statusCode(SipStatusCodes.REQUEST_TERMINATED.statusCode())
            .build());
        delegate.onComplete();
      });

    }

  }

  @Test
  void testNoTargets() {
    final SipProxyException ex = assertThrows(SipProxyException.class, () -> ProxyContext.proxy(Flowable.empty(), MaybeSubject.never()).toList().blockingGet());
    assertEquals(SipStatusCodes.TEMPORARILY_UNAVAILABLE, ex.status());
  }

  @Test
  void testSingleDownstreamFailure() {
    ProxyContext.proxy(new UpstreamLogger(), Flowable.just(new ImmediateRejection(SipStatusCodes.NOT_FOUND)), MaybeSubject.never());
  }

  @Test
  void testSingleDownstreamSuccess() {
    ProxyContext.proxy(new UpstreamLogger(), Flowable.just(new Success(Completable.complete())), MaybeSubject.never());
  }

  @Test
  void testMultipleDownstreamFailure() {
    ProxyContext.proxy(
      new UpstreamLogger(),
      Flowable.just(
        new ImmediateRejection(SipStatusCodes.NOT_FOUND),
        new ImmediateRejection(SipStatusCodes.NOT_FOUND)),
      MaybeSubject.never());
  }

  @Test
  void testPendingOnlyThenCancel() {
    final MaybeSubject<ProxyCancelReason> cancelToken = MaybeSubject.create();
    ProxyContext.proxy(
      new UpstreamLogger() {
        @Override
        public void onNext(final SipResponsePayload res) {
          System.err.println(">>> " + res);
          cancelToken.onSuccess(new ProxyCancelReason(487, "Something"));
        }
      },
      Flowable.just(new PendingOnly(SipStatusCodes.RINGING)),
      cancelToken);
  }

}
