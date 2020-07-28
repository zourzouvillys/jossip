package io.rtcore.sip.netty.codec;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public class SipResponseEncoder extends SipObjectEncoder<SipResponse> {

  @Override
  public boolean acceptOutboundMessage(Object msg) throws Exception {
    return super.acceptOutboundMessage(msg) && !(msg instanceof SipRequest);
  }

  @Override
  protected void encodeInitialLine(ByteBuf buf, SipResponse response) throws Exception {
    response.protocolVersion().encode(buf);
    buf.writeByte(SipConstants.SP);
    response.status().encode(buf);
    ByteBufUtil.writeShortBE(buf, CRLF_SHORT);
  }

}
