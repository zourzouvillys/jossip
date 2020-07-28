package io.rtcore.sip.netty.codec;


import static io.rtcore.sip.netty.codec.SipConstants.SP;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.CharsetUtil;

public class SipRequestEncoder extends SipObjectEncoder<SipRequest> {

  @Override
  public boolean acceptOutboundMessage(Object msg) throws Exception {
    return super.acceptOutboundMessage(msg) && !(msg instanceof SipResponse);
  }

  @Override
  protected void encodeInitialLine(ByteBuf buf, SipRequest request) throws Exception {

    ByteBufUtil.copy(request.method().asciiName(), buf);

    String uri = request.uri();

    if (uri.isEmpty()) {

      // we can't encode without a URI...

    }
    else {

      CharSequence uriCharSequence = uri;
      buf.writeByte(SP).writeCharSequence(uriCharSequence, CharsetUtil.UTF_8);
      buf.writeByte(SP);

    }

    request.protocolVersion().encode(buf);

    ByteBufUtil.writeShortBE(buf, CRLF_SHORT);

  }

}
