package io.rtcore.sip.generator;

import static io.rtcore.sip.message.processor.rfc3261.SipMessageManager.defaultManager;
import static io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager.defaultSerializer;

import org.junit.jupiter.api.Test;

import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.processor.rfc3261.RfcSipMessageManager;

class SipRequestGeneratorsTest {

  @Test
  void test() {

    final String buf =
      SipRequestGenerators
        .invite("sip:theo@rtcore.io")
        .generate(defaultManager())
        .apply(defaultSerializer()::writeValueAsString);

    System.err.println(buf);

  }

  @Test
  void testKeepalive() {
    final String req =
      SipRequestGenerators.options(HostAndPort.fromParts("example.com", 11111), 1)
        .generate(RfcSipMessageManager.defaultInstance())
        .apply(defaultSerializer()::writeValueAsString);
    System.err.println(req);
  }
}
