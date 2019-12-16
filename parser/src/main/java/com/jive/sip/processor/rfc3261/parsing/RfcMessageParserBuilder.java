package com.jive.sip.processor.rfc3261.parsing;

public class RfcMessageParserBuilder {

  public RfcSipMessageParser build() {
    return new DefaultRfcMessageParser();
  }

}
