package io.rtcore.sip.proxy.transport.stream;

import java.io.IOException;

import com.google.common.eventbus.EventBus;
import com.google.common.net.InternetDomainName;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import io.reactivex.rxjava3.annotations.NonNull;
import io.rtcore.sip.netty.codec.SipMessage;
import io.rtcore.sip.proxy.actions.OpenStream;

/**
 * handles the incoming messages, which are sent to the processor.
 */

public class SipStreamChannelHandler extends SimpleChannelInboundHandler<Object> {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SipStreamChannelHandler.class);

  // private SingleSubject<Channel> subject;

  // if we are connecting, the SNI we provided in connect.
  private InternetDomainName sni;
  private EventBus eventBus;
  // if channel came about because we initiated it.
  private OpenStream open;

  public SipStreamChannelHandler(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  public SipStreamChannelHandler(EventBus eventBus, OpenStream open) {
    this.eventBus = eventBus;
    this.open = open;
  }

  public static final class SipChannelActiveEvent {

    private Channel channel;
    private OpenStream open;

    public SipChannelActiveEvent(Channel channel) {
      this.channel = channel;
    }

    public SipChannelActiveEvent(Channel channel, OpenStream open) {
      this.channel = channel;
      this.open = open;
    }

    public Channel channel() {
      return this.channel;
    }

    public OpenStream open() {
      return this.open;
    }

  }

  public static final class SipChannelRemovedEvent {

    private Channel channel;

    public SipChannelRemovedEvent(Channel channel) {
      this.channel = channel;
    }

    public Channel channel() {
      return this.channel;
    }

  }

  ///

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    if (ctx.channel().isActive()) {
      eventBus.post(new SipChannelActiveEvent(ctx.channel(), this.open));
    }
    ctx.fireChannelRegistered();
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    eventBus.post(new SipChannelActiveEvent(ctx.channel(), this.open));
    ctx.fireChannelActive();
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    eventBus.post(new SipChannelRemovedEvent(ctx.channel()));
    ctx.fireChannelInactive();
  }

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    if (ctx.channel().isActive()) {
      eventBus.post(new SipChannelRemovedEvent(ctx.channel()));
    }
    ctx.fireChannelUnregistered();
  }

  ///

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

    log.info("[{}] received message: {}", ctx.channel().id().asShortText(), msg);

    if (msg instanceof SipMessage) {
      SipMessage sip = ((SipMessage) msg);
      // submit the SIP message.
    }

  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    log.trace("read completed");
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

    log.trace("[{}] {}", ctx.channel().id().asShortText(), evt);

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
    else if (evt instanceof ChannelInputShutdownReadComplete) {
      log.info("got notification of input shutdown read completion");
    }
    else {
      log.warn("unexpected userevent type {}: {}", evt.getClass(), evt);
    }

  }

  /**
   * 
   */

  private void sendKeepalive(@NonNull Channel ch) {
    log.trace("[{}] sending (CR LF CR LF) keepalive", ch.id().asShortText());
    ByteBuf buf = ch.alloc().buffer();
    ByteBufUtil.writeUtf8(buf, "\r\n\r\n");
    ch.writeAndFlush(buf);
  }

}
