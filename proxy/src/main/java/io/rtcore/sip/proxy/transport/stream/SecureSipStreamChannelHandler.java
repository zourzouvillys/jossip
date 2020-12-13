package io.rtcore.sip.proxy.transport.stream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.net.ssl.SSLSession;

import com.google.common.eventbus.EventBus;
import com.google.common.io.BaseEncoding;
import com.google.common.net.InternetDomainName;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.handler.ssl.SslCompletionEvent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.subjects.SingleSubject;
import io.rtcore.sip.proxy.actions.OpenStream;

/**
 * handles the incoming messages, which are sent to the processor.
 */

public class SecureSipStreamChannelHandler extends SimpleChannelInboundHandler<Object> {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SecureSipStreamChannelHandler.class);

  // private SingleSubject<Channel> subject;

  // if we are connecting, the SNI we provided in connect.
  private InternetDomainName sni;
  private EventBus eventBus;

  // if channel came about because we initiated it.
  private OpenStream open;

  public SecureSipStreamChannelHandler(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  public SecureSipStreamChannelHandler(EventBus eventBus, OpenStream open) {
    this.eventBus = eventBus;
    this.open = open;
  }

  public SecureSipStreamChannelHandler(EventBus eventBus, SingleSubject<Channel> subject, InternetDomainName sni) {
    // this.subject = subject;
    this.sni = sni;
    this.eventBus = eventBus;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    log.info("[{}] received message: {}", ctx.channel().id().asShortText(), msg);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    log.info("read completed");
    super.channelReadComplete(ctx);
  }

  /**
   * 
   */

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    if (cause instanceof IOException) {
      log.info("i/o exception: {}", cause.getMessage(), cause);
    }
    else {
      log.error("unhandled exception: {}", cause.getMessage(), cause);
    }

    // shut down the channel if we can.
    ctx.close();

    eventBus.post(new SipChannelEvent.ChannelFailure(ctx.channel(), cause));

    ReferenceCountUtil.release(cause);

  }

  /**
   * 
   */

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    log.info("[{}] {}", ctx.channel().id().asShortText(), evt);

    Channel ch = ctx.channel();

    if (evt instanceof IdleStateEvent) {

      IdleStateEvent e = (IdleStateEvent) evt;

      switch (e.state()) {
        case ALL_IDLE:
          break;
        case READER_IDLE:
          // no packets received for a while ...
          break;
        case WRITER_IDLE:
          sendKeepalive(ctx.channel());
          break;
        default:
          break;
      }

    }
    else if (evt instanceof SslHandshakeCompletionEvent) {

      if (((SslHandshakeCompletionEvent) evt).isSuccess()) {

        SslHandler sslhandler = ch.pipeline().get(SslHandler.class);
        SSLSession session = sslhandler.engine().getSession();

        ImmutableTlsInfo.Builder tb = ImmutableTlsInfo.builder();

        tb.local((InetSocketAddress) ch.localAddress());
        tb.remote((InetSocketAddress) ch.remoteAddress());

        if (this.sni != null) {
          tb.serverHostName(this.sni);
        }

        tb.tlsProtocol(session.getProtocol());
        tb.cipherSuite(session.getCipherSuite());
        tb.sessionId(BaseEncoding.base64Url().omitPadding().encode(session.getId()));

        try {
          tb.addAllPeerCertificates(Arrays.asList(session.getPeerCertificates()));
        }
        catch (javax.net.ssl.SSLPeerUnverifiedException ex) {
          log.debug("peer is not verified");
        }

        ch.attr(TLS_INFO_ATTR).set(tb.build());

        // subject.onSuccess(ctx.channel());

        eventBus.post(ImmutableChannelConnected.of(ctx.channel(), tb.build()));

      }
      else {

        // whatever the reason for this failure, we don't want the channel to remain open.
        ctx.channel().close();
        eventBus.post(new SipChannelEvent.ChannelFailure(ctx.channel(), ((SslHandshakeCompletionEvent) evt).cause()));
        // subject.onError(((SslHandshakeCompletionEvent) evt).cause());

      }

    }
    else if (evt instanceof SslCompletionEvent) {

      eventBus.post(new SipChannelEvent.TlsStreamCompleted(ctx.channel(), (SslCompletionEvent) evt));

      // registry.onTlsCompletion(ctx.channel(), evt);

    }
    else if (evt instanceof ProxyProtocolCompletionEvent) {

      ProxyProtocolCompletionEvent ph = (ProxyProtocolCompletionEvent) evt;

      eventBus.post(
        ImmutableChannelConnected.of(
          ch,
          ImmutableTlsInfo
            .builder()
            .local(ph.local())
            .remote(ph.remote())
            .tlsProtocol("TLSv1")
            .cipherSuite("UNKNOWN")
            .sessionId("")
            .build()));

    }
    else if (evt instanceof ChannelInputShutdownReadComplete) {
      log.info("got notification of input shutdown read completion");
    }
    else {

      log.warn("unexpected userevent type {}: {}", evt.getClass(), evt);

    }

  }

  private void sendKeepalive(@NonNull Channel ch) {
    log.info("[{}] sending (CR LF CR LF) keepalive", ch.id().asShortText());
    ByteBuf buf = Unpooled.buffer();
    buf.writeCharSequence("\r\n\r\n", StandardCharsets.UTF_8);
    ch.writeAndFlush(buf);
  }

  public static AttributeKey<ImmutableTlsInfo> TLS_INFO_ATTR = AttributeKey.newInstance("TLS_INFO_ATTR");

}
