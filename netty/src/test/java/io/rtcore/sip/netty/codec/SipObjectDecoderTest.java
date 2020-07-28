package io.rtcore.sip.netty.codec;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static java.nio.charset.StandardCharsets.UTF_8;

import org.junit.jupiter.api.Test;

import io.netty.channel.embedded.EmbeddedChannel;

public class SipObjectDecoderTest {

  @Test
  public void test() {

    EmbeddedChannel channel = new EmbeddedChannel(new SipCodec());
    
    channel.writeInbound(copiedBuffer(
      "INVITE    sip:theo@test.com SIP/2.0\r\nContent-Length: 2\r\n\r\nAB"
        + "SIP/2.0 200 OK\r\n\r\n",
      UTF_8));

    DefaultSipRequest obj = channel.readInbound();

    System.err.println(obj.method());

    if (obj.decoderResult() != null) {
      obj.decoderResult().cause().printStackTrace();
    }

    System.err.println(obj.headers());

    System.err.println(SipUtil.getContentLength(obj, -1));

    // Perform checks on your object

    Object content = channel.readInbound();
    System.err.println(content);

    System.err.println((Object) channel.readInbound());
    System.err.println((Object) channel.readInbound());

  }

}
