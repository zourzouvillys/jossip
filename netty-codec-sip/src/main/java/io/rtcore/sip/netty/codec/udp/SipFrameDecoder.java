package io.rtcore.sip.netty.codec.udp;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.google.common.collect.Iterables;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.rtcore.sip.common.ImmutableSipHeaderLine;
import io.rtcore.sip.common.SipInitialLine;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.frame.SipFrame;
import io.rtcore.sip.netty.codec.SipParsingUtils;

public class SipFrameDecoder {

  private ArrayList<ImmutableSipHeaderLine> arrayList;

  public SipFrame decode(final ByteBuf content) {
    return this.decode(content.toString(StandardCharsets.UTF_8));
  }

  private SipFrame decode(final String content) {

    final int initial = content.indexOf("\r\n");

    if (initial < "SIP/2.0".length()) {
      return null;
    }

    final SipInitialLine initialLine = SipParsingUtils.parseInitialLine(content.substring(0, initial));

    return this.parseHeaders(initialLine, content, initial);

  }

  private SipFrame parseHeaders(final SipInitialLine initialLine, final String content, final int initial) {

    final int headers = content.indexOf("\r\n\r\n", initial + 2);

    if (headers == -1) {
      return null;
    }

    final String bytes = content.substring(initial + 2, headers + 4);

    final ArrayList<ImmutableSipHeaderLine> headerLines = SipParsingUtils.parseHeaders(Unpooled.copiedBuffer(bytes, 0, bytes.length(), StandardCharsets.UTF_8));

    final int contentLength = SipParsingUtils.readContentLength(headerLines).orElse(0);

    final Iterable<ImmutableSipHeaderLine> finalHeaders = Iterables.filter(headerLines, hdr -> hdr.headerId() != StandardSipHeaders.CONTENT_LENGTH);

    if (contentLength > 0) {

      final String body = content.substring(headers + 4);

      if (body.length() != contentLength) {
        throw new DecoderException("short read");
      }

      return SipFrame.of(initialLine, finalHeaders, body);

    }

    return SipFrame.of(initialLine, finalHeaders);

  }

}
