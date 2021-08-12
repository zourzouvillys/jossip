package io.rtcore.sip.channels.netty.codec;

import java.nio.charset.StandardCharsets;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.rtcore.sip.message.message.SipMessage;

public class SipObjectEncoder extends MessageToMessageEncoder<SipMessage> {

  @Override
  protected void encode(final ChannelHandlerContext ctx, final SipMessage msg, final List<Object> out) throws Exception {
    final ByteBuf buf = ctx.alloc().buffer();
    buf.writeCharSequence(msg.asString(), StandardCharsets.UTF_8);
    out.add(buf);
  }

}
