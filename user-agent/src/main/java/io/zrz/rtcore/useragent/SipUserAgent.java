package io.zrz.rtcore.useragent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import io.netty.channel.nio.NioEventLoopGroup;
import io.reactivex.rxjava3.core.Flowable;
import io.rtcore.sip.channels.api.SipClientExchange.Event;
import io.rtcore.sip.channels.api.SipFrame;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.api.SipResponseFrame;
import io.rtcore.sip.channels.connection.SipConnection;
import io.rtcore.sip.channels.connection.SipRoute;
import io.rtcore.sip.channels.errors.SipError;
import io.rtcore.sip.channels.netty.tcp.SipConnectionPool;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.uri.SipUri;

public class SipUserAgent {

  private final NioEventLoopGroup eventLoop;
  private final SipConnectionPool pool;
  private final SipUri self;

  public SipUserAgent(SipUri self) {
    this.eventLoop = new NioEventLoopGroup();
    this.pool = SipConnectionPool.createTcpPool(eventLoop);
    this.self = self;
  }

  /**
   * 
   */

  public Flowable<Event> invite(Consumer<ImmutableInviteRequest.Builder> b) {
    var builder = ImmutableInviteRequest.builder();
    b.accept(builder);
    return invite(builder.build());
  }

  /**
   * 
   */

  public Flowable<Event> invite(InviteRequest req) {

    return Flowable.defer(() -> {

      SipConnection conn = this.pool.requestConnection(req.target());

      String localTag = generateTag(5);
      String callId = generateTag(20);

      String offer = req.offer();

      String ruri = req.uri().uri().toASCIIString();
      String body = offer;

      List<SipHeaderLine> headers = new ArrayList<>();

      headers.addAll(List.of(
        SipHeaderLine.of("To", NameAddr.of(req.uri()).encode()),
        SipHeaderLine.of("From", NameAddr.of(req.from()).withTag(localTag).encode()),
        SipHeaderLine.of("Call-ID", callId),
        SipHeaderLine.of("CSeq", "1 INVITE"),
        SipHeaderLine.of("Contact", NameAddr.of(this.self).encode()),
        SipHeaderLine.of("Content-Type", "application/sdp"),
        SipHeaderLine.of("Content-Length", Integer.toString(body.length()))
      //
      ));

      headers.addAll(req.authenticator().map(x -> x.generate(SipMethods.INVITE, ruri, body)).orElse(List.of()));

      SipRequestFrame frame =
        SipFrame.of(
          SipMethods.INVITE,
          req.uri().uri(),
          headers,
          body);

      return conn
        .exchange(frame)
        .responses()
        .doOnNext(res -> req.authenticator().ifPresent(authctx -> authctx.observe(res)))
        .doOnError(t -> t.printStackTrace())
        .doOnTerminate(() -> conn.close())
        .map(e -> {
          // throw all failures as pipeline errors.
          if (e.response().initialLine().code() >= 300) {
            throw new SipError(e.response());
          }
          return e;
        });

    });

  }

  private void ack(SipResponseFrame res, SipRoute route) {

    // extract contaxt

    List<SipHeaderLine> headerLines = res.headerLines();

    SipUri target = UserAgentUtils.singleSipContact(headerLines).get();

    List<SipHeaderLine> headers = new ArrayList<>();

    headers.addAll(List.of(
      SipHeaderLine.of("Via", "SIP/2.0/TCP invalid;rport;branch=" + generateTag(12)),
      SipHeaderLine.of("To", UserAgentUtils.singleOrThrow(headerLines, StandardSipHeaders.TO)),
      SipHeaderLine.of("From", UserAgentUtils.singleOrThrow(headerLines, StandardSipHeaders.FROM)),
      SipHeaderLine.of("Call-ID", UserAgentUtils.singleOrThrow(headerLines, StandardSipHeaders.CALL_ID)),
      SipHeaderLine.of("CSeq", "1 ACK"),
      SipHeaderLine.of("Content-Length", Integer.toString(0))
    //
    ));

    SipRequestFrame frame =
      SipFrame.of(
        SipMethods.ACK,
        target.uri(),
        headers,
        null);

    SipConnection conn = this.pool.requestConnection(route);

    conn
      .send(frame)
      .thenRun(() -> conn.close());

  }

  private String generateTag(int length) {
    byte[] bytes = new byte[length];
    ThreadLocalRandom.current().nextBytes(bytes);
    return Base62.base62Encode(bytes);
  }

}
