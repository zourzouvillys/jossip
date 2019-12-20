package io.rtcore.sip.message.generator;

import static io.rtcore.sip.message.processor.rfc3261.SipMessageManager.defaultManager;
import static io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager.defaultSerializer;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.generator.SipRequestGenerators;

class SipRequestGeneratorsTest {

  @Test
  void test() {

    String buf =
      SipRequestGenerators
        .invite("sip:theo@rtcore.io")
        .generate(defaultManager())
        .apply(defaultSerializer()::writeValueAsString);

    System.err.println(buf);

  }

}
