package io.rtcore.sip.message.processor.rfc3261.parsing;

public class RfcMessageParserBuilder {

  public RfcSipMessageParser build() {
    return new DefaultRfcMessageParser();
  }

}
