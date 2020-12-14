package io.rtcore.sip.proxy.http.netty;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

/**
 * a netty handler mapping to a StreamContext.
 */

@Sharable
public class StreamContextHandler extends ChannelInboundHandlerAdapter {

  private final static AttributeKey<StreamContext> APPHANDLE = AttributeKey.valueOf("apphandle");

  // dispatcher which handles new incoming streams.
  private final StreamDispatcher dispatcher;

  public StreamContextHandler(StreamDispatcher dispatcher) {
    this.dispatcher = dispatcher;
  }

  /**
   * 
   */

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) {
    ctx.channel().attr(APPHANDLE).set(new StreamContext(ctx, dispatcher));
    ctx.fireChannelRegistered();
  }

  /**
   * incoming channel read for a stream.
   */

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (!ctx.channel().attr(APPHANDLE).get().read(msg)) {
      ctx.fireChannelRead(msg);
    }
  }

  /**
   * the read request has complete,d if we are not auto reading should ask for more.
   */

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    if (!ctx.channel().attr(APPHANDLE).get().readComplete()) {
      ctx.fireChannelReadComplete();
    }
  }

  /**
   * incoming channel read for a stream.
   */

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (!ctx.channel().attr(APPHANDLE).get().userEventTriggered(evt)) {
      ctx.fireUserEventTriggered(evt);
    }
  }

  /**
   * an exception is caught in pipeline processing.
   */

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.channel().attr(APPHANDLE).get().exceptionCaught(cause);
    ctx.fireExceptionCaught(cause);
  }

  /**
   * when a stream is closed/removed.
   */

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) {
    StreamContext buf = ctx.channel().attr(APPHANDLE).getAndSet(null);
    if (buf != null) {
      buf.closed();
    }
    ctx.fireChannelUnregistered();
  }
}
