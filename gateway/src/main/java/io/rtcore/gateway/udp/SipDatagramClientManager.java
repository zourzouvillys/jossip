package io.rtcore.gateway.udp;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.apache.commons.rng.core.source64.MersenneTwister64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.FlowableProcessor;
import io.reactivex.rxjava3.processors.ReplayProcessor;
import io.reactivex.rxjava3.subjects.CompletableSubject;
import io.reactivex.rxjava3.subjects.MaybeSubject;
import io.rtcore.gateway.engine.sip.ClientBranchId;
import io.rtcore.gateway.engine.sip.NetworkSegment;
import io.rtcore.gateway.engine.sip.NetworkSegment.ClientBranchRegistration;
import io.rtcore.sip.common.HostPort;
import io.rtcore.sip.common.IpAndPort;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipStatusCategory;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.frame.SipResponseFrame;
import io.rtcore.sip.message.message.api.BranchId;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.message.message.api.ViaProtocol;
import io.rtcore.sip.message.uri.SipUri;
import io.rtcore.sip.netty.codec.SipParsingUtils;
import io.rtcore.sip.netty.codec.SipParsingUtils.TopViaRemovalResult;

/**
 * manages SIP datagram based on client transactions.
 */

public class SipDatagramClientManager {

  private final HashedWheelTimer timer = new HashedWheelTimer(50, TimeUnit.MILLISECONDS);

  private static final Logger LOG = LoggerFactory.getLogger(SipDatagramClientManager.class);

  private static final List<Duration> TRANSMIT_SCHEDULE =
    List.of(
      Duration.ofMillis(500),
      Duration.ofSeconds(1),
      Duration.ofSeconds(2),
      // Duration.ofSeconds(4),
      // Duration.ofSeconds(4),
      // Duration.ofSeconds(4),
      // Duration.ofSeconds(4),
      // Duration.ofSeconds(4),
      Duration.ofSeconds(4)
    //
    );

  private final MersenneTwister64 rng = new MersenneTwister64(new long[] { ThreadLocalRandom.current().nextLong() });

  private final NetworkSegment segment;
  private final SipDatagramSocket udp;
  private final HostPort self;

  public SipDatagramClientManager(final NetworkSegment segment, final SipDatagramSocket udp) {
    this.segment = segment;
    this.udp = udp;
    this.self = IpAndPort.of(udp.localSocketAddress()).toHostPort();
  }

  public Flowable<SipResponseFrame> transmit(final SipRequestFrame request, final InetSocketAddress recipient) {
    try {
      return switch (request.initialLine().method().token()) {
        case "ACK" -> {
          // ACK only transmits, and never receives responses. completes immediately after
          // transmission.
          yield Completable.fromCompletionStage(this.udp.transmit(recipient, request)).toFlowable();
        }
        case "INVITE" -> {
          yield this.transmitInvite(request, recipient);
        }
        default -> {
          yield this.transmitNonInvite(request, recipient);
        }
      };
    }
    catch (final Exception e) {
      LOG.warn("error transmitting frame", e.getMessage(), e);
      return Flowable.error(e);
    }
  }

  /**
   * handle an INVITE, which differs from other request/responses in that we stream responses until
   * the transaction terminates. this can be a long time, and may include multiple 2xx
   * transmissions.
   *
   * unlike NICTs, we don't absorb retransmits of 1xx or 2xx responses. However, we do absorb any
   * retransmits of 100, and >= 3XX.
   *
   * We ensure that no provisional are sent after we have received a final response.
   *
   * For failures, we are responsible for sending the ACK (in the same branch). However, for
   * success, we are not. They get sent end to end.
   *
   * @param request
   *          the request to send, excluding the top Via (which we will generate).
   * @param recipient
   *          where to send it.
   *
   * @return a flow which completes once the transmission will no longer provide any responses.
   */

  private Flowable<SipResponseFrame> transmitInvite(SipRequestFrame request, final InetSocketAddress recipient) {

    LOG.info("transmitting INVITE to {}: {}", recipient, request);

    //
    final List<SipHeaderLine> headers = new ArrayList<>(request.headerLines());

    // add our Max-Forwards header if missing.
    if (Iterables.isEmpty(StandardSipHeaders.MAX_FORWARDS.all(headers))) {
      headers.add(StandardSipHeaders.MAX_FORWARDS.ofLine("70"));
    }

    // add our From header if missing.
    if (Iterables.isEmpty(StandardSipHeaders.FROM.all(headers))) {
      headers.add(StandardSipHeaders.FROM.ofLine(NameAddr.of(SipUri.ANONYMOUS)
        .withTag(this.generateLocalTag())
        .encode()));
    }

    // add our To header if missing.
    if (Iterables.isEmpty(StandardSipHeaders.TO.all(headers))) {
      headers.add(StandardSipHeaders.TO.ofLine("<" + request.initialLine().uri().toASCIIString() + ">"));
    }

    // add CSeq header if missing.
    if (Iterables.isEmpty(StandardSipHeaders.CSEQ.all(headers))) {
      headers.add(StandardSipHeaders.CSEQ.ofLine("1 " + request.initialLine().method().token()));
    }

    // add Call-ID header if missing.
    if (Iterables.isEmpty(StandardSipHeaders.CALL_ID.all(headers))) {
      headers.add(StandardSipHeaders.CALL_ID.ofLine(this.generateCallId()));
    }

    // add Call-ID header if missing.
    if (Iterables.isEmpty(StandardSipHeaders.CONTACT.all(headers))) {
      // TODO: resolve this to a local contact which will route down this flow.
      headers.add(StandardSipHeaders.CONTACT.ofLine(NameAddr.of(SipUri.ANONYMOUS).encode()));
    }

    // --- Via header handling.

    // create a new transaction branch, 8 random characters.
    final String branchId = this.generateBranchId();

    // add the Via header
    final Via topVia =
      Via.of(
        ViaProtocol.UDP,
        this.self,
        BranchId.withCookiePrepended(branchId))
        .withRPort();

    // add our Via at the top.
    headers.add(0, StandardSipHeaders.VIA.ofLine(topVia.encode()));

    final ClientBranchId transactionId =
      new ClientBranchId(
        topVia.sentBy(),
        request.initialLine().method(),
        branchId);

    LOG.info("ICT transactionId: {}", transactionId);

    final FlowableProcessor<SipResponseFrame> responses = ReplayProcessor.create();

    // handle is called when the transaction is complete.
    final ClientBranchRegistration handle =
      this.segment.allocateClientTransaction(
        transactionId,
        new InviteResponseHandler(responses));

    // calculate all the headers we need to add.
    request = request.withHeaderLines(headers);

    // we first send the request, and then stream all the responses.
    final SipRequestFrame tx = request;


    // continue to transmit as long as we don't have a response.
    final Completable source =
      this.scheduleUntil(
        TRANSMIT_SCHEDULE,
        responses.ignoreElements(),
        () -> this.udp.transmit(recipient, tx));

    // 'source' is re-transmitter. once we're done sending, mark as complete.
    source.subscribe(
      responses::onComplete,
      responses::onError);

    // TODO: retransmit until we get a response or timeout.
    return responses
      // TODO: do we even want to handle UDP transport failures? they're not reliable indicators,
      // and can be spoofed easily. for now, just print and continue transmitting.
      .doOnError(@NonNull Throwable::printStackTrace)
      //
      .doOnTerminate(() -> handle.remove(Duration.ofSeconds(32)));

  }

  /**
   * handles the incoming responses within a context, to remove duplicates and massage to remove the
   * top Via header field.
   */

  private static class InviteResponseHandler implements Consumer<SipResponseFrame> {

    private final FlowableProcessor<SipResponseFrame> responder;

    InviteResponseHandler(final FlowableProcessor<SipResponseFrame> responses) {
      this.responder = responses;
    }

    @Override
    public void accept(SipResponseFrame response) {

      final SipStatusCategory status = SipStatusCategory.forCode(response.initialLine().code());

      // remove the top Via header field.
      // we can't just remove the whole line, as it may contain
      // other field values that just ours.
      final TopViaRemovalResult modified = SipParsingUtils.removeTopViaHeader(response.headerLines());

      if (modified.topVia().isEmpty()) {
        LOG.error("no Via header found in response: {}", response);
        return;
      }

      LOG.info("received ICT {} response: {}, {}", status, response.initialLine(), modified.topVia());

      // replace with the modified headers that doesn't include the first Via field
      response = response.withHeaderLines(modified.headers());

      // notify listener.
      this.responder.onNext(response);

      if (status.isFailure()) {
        this.responder.onComplete();
      }

    }

  }

  private Flowable<SipResponseFrame> transmitNonInvite(SipRequestFrame request, final InetSocketAddress recipient) {

    //
    LOG.debug("transmitting {} to {}: {}", request.initialLine().method(), recipient, request);

    //
    final List<SipHeaderLine> headers = new ArrayList<>(request.headerLines());

    // add our Max-Forwards header if missing.
    if (Iterables.isEmpty(StandardSipHeaders.MAX_FORWARDS.all(headers))) {
      headers.add(StandardSipHeaders.MAX_FORWARDS.ofLine("70"));
    }

    // add our From header if missing.
    if (Iterables.isEmpty(StandardSipHeaders.FROM.all(headers))) {
      headers.add(StandardSipHeaders.FROM.ofLine(NameAddr.of(SipUri.ANONYMOUS)
        .withTag(this.generateLocalTag())
        .encode()));
    }

    // add our To header if missing.
    if (Iterables.isEmpty(StandardSipHeaders.TO.all(headers))) {
      headers.add(StandardSipHeaders.TO.ofLine("<" + request.initialLine().uri().toASCIIString() + ">"));
    }

    // add CSeq header if missing.
    if (Iterables.isEmpty(StandardSipHeaders.CSEQ.all(headers))) {
      headers.add(StandardSipHeaders.CSEQ.ofLine("1 " + request.initialLine().method().token()));
    }

    // add Call-ID header if missing.
    if (Iterables.isEmpty(StandardSipHeaders.CALL_ID.all(headers))) {
      headers.add(StandardSipHeaders.CALL_ID.ofLine(this.generateCallId()));
    }

    // --- Via header handling.

    // create a new transaction branch, 8 random characters.
    final String branchId = this.generateBranchId();

    // add the Via header
    final Via topVia =
      Via.of(
        ViaProtocol.UDP,
        this.self,
        BranchId.withCookiePrepended(branchId))
        .withRPort();

    // add our Via at the top.
    headers.add(0, StandardSipHeaders.VIA.ofLine(topVia.encode()));

    final ClientBranchId transactionId =
      new ClientBranchId(
        topVia.sentBy(),
        request.initialLine().method(),
        branchId);

    LOG.info("transactionId: {}", transactionId);

    final MaybeSubject<SipResponseFrame> responses = MaybeSubject.create();

    // handle is called when the transaction is complete.
    final ClientBranchRegistration handle =
      this.segment.allocateClientTransaction(
        transactionId,
        new NICTResponseHandler(response -> responses.onSuccess(response)));

    // calculate all the headers we need to add.
    request = request.withHeaderLines(headers);

    // we first send the request, and then stream all the responses.
    final SipRequestFrame tx = request;

    // continue to transmit as long as we don't have a response.
    final Completable source =
      this.scheduleUntil(
        TRANSMIT_SCHEDULE,
        responses.ignoreElement(),
        () -> this.udp.transmit(recipient, tx));

    // 'source' is re-transmitter. once we're done sending, mark as complete.
    source.subscribe(
      responses::onComplete,
      responses::onError);

    // TODO: retransmit until we get a response or timeout.
    return responses
      // TODO: do we even want to handle UDP transport failures? they're not reliable indicators,
      // and can be spoofed easily. for now, just print and continue transmitting.
      .doOnError(@NonNull Throwable::printStackTrace)
      // TODO(https://github.com/zourzouvillys/jossip/issues/265: i'm not convinced that we need to
      // absorb. sets set the branch to indicate if the client has gone we just dispose rather than
      // forward, and if we should ever want to forward responses without state, encode it in the
      // branch. we could also mark the branch to include a signed timestamp and very quickly detect
      // responses which are clearly misrouted, and fast dropping.
      .doOnTerminate(() -> handle.remove(Duration.ofSeconds(32)))
      .toFlowable();

  }

  /**
   *
   * @param intervals
   * @param responses
   * @param run
   * @return
   */

  private Completable scheduleUntil(final Iterable<Duration> intervals, final Completable until, final Runnable invoker) {
    final AtomicReference<Timeout> current = new AtomicReference<>();
    final CompletableSubject s = CompletableSubject.create();
    until.subscribe(
      () -> this.tryCancel(current),
      err -> this.tryCancel(current));
    final Iterator<Duration> it = intervals.iterator();
    this.schedule(it, s, until, invoker, current);
    return s;
  }

  private void tryCancel(final AtomicReference<Timeout> current) {
    final Timeout t = current.get();
    if (t != null) {
      t.cancel();
    }
  }

  private
      void
      schedule(
          final Iterator<Duration> it,
          final CompletableSubject sink,
          final Completable until,
          final Runnable invoker,
          final AtomicReference<Timeout> current) {

    try {
      LOG.info("transmitting");
      invoker.run();
    }
    catch (final Exception ex) {
      LOG.warn("error invoking send", ex);
      // continue, trying again.
    }

    if (!it.hasNext()) {
      LOG.info("transmit complete, timing out");
      // no more, so mask as complete.
      sink.onComplete();
      return;
    }

    final Duration next = it.next();

    current.set(this.timer.newTimeout(timeout -> this.schedule(it, sink, until, invoker, current), next.toMillis(), TimeUnit.MILLISECONDS));

  }

  /**
   * handles the incoming responses within a context, to remove duplicates and massage to remove the
   * top Via header field.
   */

  private static class NICTResponseHandler implements Consumer<SipResponseFrame> {

    private final Consumer<SipResponseFrame> responder;

    NICTResponseHandler(final Consumer<SipResponseFrame> responder) {
      this.responder = responder;
    }

    @Override
    public void accept(SipResponseFrame response) {

      if (response.initialLine().code() < 200) {
        // ignore provisional responses.
        LOG.info("ignoring provisional response for NICT: {}", response.initialLine());
        return;
      }

      // remove the top Via header field.
      // we can't just remove the whole line, as it may contain
      // other field values that just ours.
      final TopViaRemovalResult modified = SipParsingUtils.removeTopViaHeader(response.headerLines());

      if (modified.topVia().isEmpty()) {
        LOG.error("no Via header found in response: {}", response);
        return;
      }

      LOG.info("received response: {}, {}", response.initialLine(), modified.topVia());

      // replace with the modified headers that doesn't include the first Via field
      response = response.withHeaderLines(modified.headers());

      // notify listener.
      this.responder.accept(response);

    }

  }

  // return 8 random alphanumeric characters.
  private String generateBranchId() {
    return BigInteger.valueOf(this.rng.nextLong()).abs().toString(36);
    // return String.format("%016x", this.rng.nextLong());
  }

  // return 8 random alphanumeric characters.
  private String generateCallId() {
    return BigInteger.valueOf(this.rng.nextLong()).abs().toString(36);
  }

  // return 8 random alphanumeric characters.
  private String generateLocalTag() {
    return BigInteger.valueOf(this.rng.nextLong()).abs().toString(36).substring(0, 8);
  }

}
