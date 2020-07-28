package io.rtcore.sip.netty.codec;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import io.netty.channel.embedded.EmbeddedChannel;

public class SipObjectAggregatorTest {

  @Test
  public void test() {

    EmbeddedChannel channel = new EmbeddedChannel(new SipCodec(), new SipObjectAggregator(65536));

    channel.writeInbound(copiedBuffer(
      "INVITE    sip:theo@test.com SIP/2.0\r\nContent-Length: 2\r\n\r\nAB"
        + "SIP/2.0 200 OK\r\n\r\n",
      UTF_8));

    FullSipRequest obj = channel.readInbound();

    assertEquals(SipMethod.INVITE, obj.method());
    assertEquals(2, obj.content().readableBytes());
    
    assertNull(obj.decoderResult());

  }

}
