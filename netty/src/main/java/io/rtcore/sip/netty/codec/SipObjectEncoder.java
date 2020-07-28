package io.rtcore.sip.netty.codec;


import static io.rtcore.sip.netty.codec.SipConstants.CR;
import static io.rtcore.sip.netty.codec.SipConstants.LF;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public abstract class SipObjectEncoder<H extends SipMessage> extends MessageToMessageEncoder<SipMessage> {

  static final int CRLF_SHORT = (CR << 8) | LF;

  @Override
  protected void encode(ChannelHandlerContext ctx, SipMessage msg, List<Object> out) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: MessageToMessageEncoder<Object>.encode invoked.");
  }

  protected abstract void encodeInitialLine(ByteBuf buf, H message) throws Exception;

}
