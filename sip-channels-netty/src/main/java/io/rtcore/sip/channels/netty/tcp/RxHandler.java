package io.rtcore.sip.channels.netty.tcp;

import java.util.function.Consumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.rtcore.sip.frame.SipFrame;

/**
 * handler which publishes incoming messages to a specified publisher.
 */

class RxHandler extends SimpleChannelInboundHandler<SipFrame> {

  private Consumer<SipFrame> publisher;

  RxHandler(Consumer<SipFrame> in) {
    this.publisher = in;
  }

  /**
   * 
   */

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, SipFrame frame) throws Exception {
    this.publisher.accept(frame);
  }

}
