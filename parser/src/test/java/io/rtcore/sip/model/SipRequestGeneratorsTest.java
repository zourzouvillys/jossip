package io.rtcore.sip.model;

import static com.jive.sip.processor.rfc3261.SipMessageManager.defaultManager;
import static com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager.defaultSerializer;

import org.junit.jupiter.api.Test;

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
