package io.rtcore.sip.channels.netty.codec;

import java.nio.charset.StandardCharsets;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.ReferenceCountUtil;
import io.rtcore.sip.channels.api.SipFrame;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.SipInitialLine;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.message.message.SipMessage;

public class SipObjectEncoder extends MessageToMessageEncoder<Object> {

  @Override
  protected void encode(final ChannelHandlerContext ctx, final Object in, final List<Object> out) throws Exception {

    if (in instanceof SipFrame frame) {

      final ByteBuf buf = ctx.alloc().buffer();

      if (frame.initialLine() instanceof SipInitialLine.RequestLine req) {
        buf.writeCharSequence(req.method().token() + " ", StandardCharsets.US_ASCII);
        buf.writeCharSequence(req.uri().toASCIIString(), StandardCharsets.US_ASCII);
        buf.writeCharSequence(" SIP/2.0\r\n", StandardCharsets.US_ASCII);
      }
      else if (frame.initialLine() instanceof SipInitialLine.ResponseLine res) {
        buf.writeCharSequence("SIP/2.0 " + Integer.toString(res.code()) + " ", StandardCharsets.US_ASCII);
        buf.writeCharSequence(res.reason().orElse("Unknown"), StandardCharsets.US_ASCII);
        buf.writeCharSequence("\r\n", StandardCharsets.US_ASCII);
      }

      for (SipHeaderLine line : frame.headerLines()) {

        if (line.knownHeaderId().orElse(null) == StandardSipHeaders.CONTENT_LENGTH) {
          continue;
        }

        buf.writeCharSequence(line.headerName() + ": " + line.headerValues() + "\r\n", StandardCharsets.UTF_8);

      }

      frame.body()
        .ifPresentOrElse(
          body -> {

            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

            buf.writeCharSequence(
              String.format("Content-Length: %s\r\n\r\n", bytes.length),
              StandardCharsets.UTF_8);

            buf.writeBytes(bytes);

          },
          () -> buf.writeCharSequence("Content-Length: 0\r\n\r\n", StandardCharsets.UTF_8));

      out.add(buf);

    }
    else if (in instanceof SipMessage msg) {
      final ByteBuf buf = ctx.alloc().buffer();
      buf.writeCharSequence(msg.asString(), StandardCharsets.UTF_8);
      out.add(buf);
    }
    else {
      out.add(ReferenceCountUtil.retain(in));
    }
  }

}
