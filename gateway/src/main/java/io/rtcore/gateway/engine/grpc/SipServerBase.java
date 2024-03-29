package io.rtcore.gateway.engine.grpc;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.google.protobuf.ByteString;

import io.grpc.Status;
import io.grpc.StatusException;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.rtcore.gateway.proto.Rx3SipServerGrpc;
import io.rtcore.gateway.proto.SipBody;
import io.rtcore.gateway.proto.SipExchangeReply;
import io.rtcore.gateway.proto.SipExchangeRequest;
import io.rtcore.gateway.proto.SipHeader;
import io.rtcore.gateway.proto.SipRequest;
import io.rtcore.gateway.proto.SipResponse;
import io.rtcore.gateway.udp.SipDatagramClientManager;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.frame.SipResponseFrame;

public class SipServerBase extends Rx3SipServerGrpc.SipServerImplBase {

  private static final Logger LOG = LoggerFactory.getLogger(SipServerBase.class);
  private final SipDatagramClientManager client;

  public SipServerBase(final SipDatagramClientManager client) {
    this.client = Objects.requireNonNull(client);
  }

  @Override
  public Flowable<SipExchangeReply> exchange(final SipExchangeRequest request) {

    LOG.info("incoming exchange: {}", request);

    if (Strings.isNullOrEmpty(request.getConnectionId())) {
      return Flowable.error(new StatusException(Status.INVALID_ARGUMENT.withDescription("connectionId is required")));
    }

    return switch (request.getFrame().getMethod()) {
      case "ACK" -> {
        yield new AckExchangeContext(request).events();
      }
      case "INVITE" -> {
        final InviteExchangeContext ctx = new InviteExchangeContext(request);
        yield ctx.events();
      }
      case "CANCEL" -> {
        // standard NICT.
        final NonInviteExchangeContext ctx = new NonInviteExchangeContext(request);
        yield ctx.events();
      }
      default -> {
        // standard NICT.
        final NonInviteExchangeContext ctx = new NonInviteExchangeContext(request);
        yield ctx.events();
      }
    };
  }

  /**
   * handle transmission of an ACK.
   *
   * because an ACK is a one-shot thing, it is not cancellable. the only thing that can occur is an
   * error on transmission.
   *
   */

  private class AckExchangeContext {

    private final Flowable<SipExchangeReply> events;

    public AckExchangeContext(final SipExchangeRequest request) {
      this.events =
        SipServerBase.this.client.transmit(SipServerBase.this.makeFrame(request.getFrame()), SipServerBase.this.makeTarget(request))
          .map(SipServerBase.this::makeEvent);
    }

    public Flowable<SipExchangeReply> events() {
      return this.events;
    }

  }

  /**
   * handle transmission of an INVITE, and reception of the responses.
   *
   * the only valid sequence of responses is 100?, 1XX*, (2XX+ | [3-6XX])).
   *
   * We handle a termination of of the stream by generating a CANCEL if possible (and it has not
   * already been cancelled). This is only for the edge case where the upstream (client) looses
   * connectivity to us, and thus we can't send responses back to it. Typical behavior instead would
   * be to send a CANCEL exchange to the same flow and with the same idempotent identifier (which
   * will result in the CANCEL having the same branch as the original request). Note that per SIP, a
   * CANCEL can not be sent until a 100 has been received.
   *
   */

  private class InviteExchangeContext {

    private final @NonNull Flowable<@NonNull SipExchangeReply> processor;

    public InviteExchangeContext(final SipExchangeRequest request) {
      this.processor =
        SipServerBase.this.client.transmit(SipServerBase.this.makeFrame(request.getFrame()), SipServerBase.this.makeTarget(request))
          .map(SipServerBase.this::makeEvent);
    }

    public Flowable<SipExchangeReply> events() {
      return this.processor;
    }

    private void cancel() {
      // this is an upstream (caller) failure. so we should try to CANCEL if
      // possible (and not already CANCELled).
      LOG.warn("TODO: caller disconnected, should CANCEL");
    }

  }

  /**
   * each gRPC request is handled within the scope of this context.
   */

  private class NonInviteExchangeContext {

    private final @NonNull Flowable<SipExchangeReply> processor;

    // only used for diagnostics logging.
    private volatile boolean complete;

    public NonInviteExchangeContext(final SipExchangeRequest request) {
      this.processor =
        SipServerBase.this.client.transmit(SipServerBase.this.makeFrame(request.getFrame()), SipServerBase.this.makeTarget(request))
          .map(SipServerBase.this::makeEvent)
          .doOnComplete(this::complete)
          .doOnCancel(this::cancel);
    }

    public Flowable<SipExchangeReply> events() {
      return this.processor;
    }

    private void complete() {
      this.complete = true;
    }

    private void cancel() {

      // nothing to be done when cancelling a non-invite, as it can't be cancelled.
      // maybe one day we'll be scheduling/buffering requests and this would be used
      // to abort the wait.
      if (!this.complete) {
        LOG.info("caller disconnected after transmissing non-INVITE");
      }

    }

  }

  /////////////////////////////////////////////////////////////////////////////
  // helpers to convert between SIP frames and gRPC messages.
  /////////////////////////////////////////////////////////////////////////////

  private SipRequestFrame makeFrame(final SipRequest frame) {
    // convert the 'frame' to a SipRequestFrame:
    return SipRequestFrame.of(
      SipMethods.toMethodId(frame.getMethod()),
      URI.create(frame.getUri()),
      this.makeHeaders(frame.getHeadersList()),
      Optional.ofNullable(frame.getBody().getBinary()).map(b -> b.toString(StandardCharsets.UTF_8)));
  }

  public InetSocketAddress makeTarget(final SipExchangeRequest request) {
    final String connectionId = request.getConnectionId();
    final HostAndPort target = HostAndPort.fromString(connectionId);
    return new InetSocketAddress(
      InetAddresses.forString(target.getHost()),
      target.getPortOrDefault(5060));
  }

  private SipResponse makeFrame(final SipResponseFrame frame) {

    final SipResponse.Builder b = SipResponse.newBuilder();

    b.setStatusCode(frame.initialLine().code());
    frame.initialLine().reason().ifPresent(b::setReasonPhrase);

    b.addAllHeaders(this.toProto(frame.headerLines()));

    frame.body().ifPresent(body -> b.setBody(SipBody.newBuilder().setBinary(ByteString.copyFromUtf8(body))));

    return b.build();

  }

  private Iterable<? extends SipHeader> toProto(final List<SipHeaderLine> headerLines) {
    return headerLines.stream()
      .map(h -> SipHeader.newBuilder().setName(h.headerName()).addValues(h.headerValues()).build())
      .toList();
  }

  private List<SipHeaderLine> makeHeaders(final List<SipHeader> headerList) {
    final Function<SipHeader, Stream<SipHeaderLine>> f =
      h -> h.getValuesList()
        .stream()
        .map(v -> SipHeaderLine.of(h.getName(), v));
    return headerList.stream().flatMap(f).toList();
  }

  private SipExchangeReply makeEvent(final SipResponseFrame res) {
    final SipExchangeReply.Builder b = SipExchangeReply.newBuilder();
    b.setFrame(this.makeFrame(res));
    return b.build();
  }

}
