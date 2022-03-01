package io.rtcore.sip.channels.netty.codec;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.netty.channel.embedded.EmbeddedChannel;

class SipStreamDecoderTest {

  @Test
  void testInitialLine() {

    EmbeddedChannel ch = new EmbeddedChannel(new SipStreamDecoder());

    assertTrue(
      ch.writeInbound(copiedBuffer("OPTIONS sip:invalid SIP/2.0\r\nVia: something\r\n\tgelloconsif\r\ncontent-length: 0\r\nCSeq: 1 INVITE\r\n\r\n", UTF_8)));

    assertTrue(ch.finish());

    System.err.println((Object) ch.readInbound());

  }

  @Test
  void testCRLFs() {

    EmbeddedChannel ch = new EmbeddedChannel(new SipStreamDecoder());

    assertFalse(ch.writeInbound(copiedBuffer("\r\n\r\n", UTF_8)));
    assertFalse(ch.finish());

  }

  @Test
  void testSingle() {

    EmbeddedChannel ch = new EmbeddedChannel(new SipStreamDecoder());

    ch.writeInbound(copiedBuffer("\r", UTF_8));
    ch.writeInbound(copiedBuffer("\n\r\n", UTF_8));

    assertFalse(ch.finish());

  }

}
