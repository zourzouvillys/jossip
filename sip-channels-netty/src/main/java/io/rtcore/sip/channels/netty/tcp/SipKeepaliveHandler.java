package io.rtcore.sip.channels.netty.tcp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleUserEventChannelHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import io.rtcore.sip.netty.codec.SipKeepalive;

public class SipKeepaliveHandler extends SimpleUserEventChannelHandler<Object> {

  @Override
  protected void eventReceived(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent idle) {
      if (idle.isFirst() && (idle.state() == IdleState.WRITER_IDLE || idle.state() == IdleState.ALL_IDLE)) {
        ctx.writeAndFlush(Unpooled.wrappedBuffer("\r\n\r\n".getBytes()));
      }
    }
    else if (evt instanceof SipKeepalive keepalive) {
      //
    }
    else {
      ctx.fireUserEventTriggered(ReferenceCountUtil.retain(evt));
    }
  }

}
