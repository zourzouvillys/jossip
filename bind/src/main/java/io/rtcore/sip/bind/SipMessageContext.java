package io.rtcore.sip.bind;

import java.nio.charset.StandardCharsets;

import io.rtcore.sip.message.processor.rfc3261.RfcSipMessageManager;
import io.rtcore.sip.message.processor.rfc3261.RfcSipMessageManagerBuilder;
import io.rtcore.sip.message.processor.rfc3261.parsing.DefaultRfcMessageParser;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManagerBuilder;

public class SipMessageContext {

  private static RfcSipMessageManager manager = new RfcSipMessageManagerBuilder().build();
  private static DefaultRfcMessageParser parser = new DefaultRfcMessageParser();
  private static RfcSerializerManager serializer = new RfcSerializerManagerBuilder().build();

  public static SipMessageContext fromBytes(byte[] bytes) {
    return fromUtf8(new String(bytes, StandardCharsets.UTF_8));
  }

  public static SipMessageContext fromUtf8(String message) {

    // find the first \n.
    int firstLine = message.indexOf('\n');

    if (firstLine == -1) {
      throw new SipParseException("");
    }

    return null;

  }

}
