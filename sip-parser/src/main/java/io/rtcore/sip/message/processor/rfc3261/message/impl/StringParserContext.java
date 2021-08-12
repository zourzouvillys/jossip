package io.rtcore.sip.message.processor.rfc3261.message.impl;

import java.nio.charset.StandardCharsets;

import io.rtcore.sip.message.processor.rfc3261.RfcSipMessageManager;
import io.rtcore.sip.message.processor.rfc3261.parsing.AbstractStringParseContext;

public class StringParserContext extends AbstractStringParseContext {

  private final String name;

  public StringParserContext(final String value) {
    super(null, value.getBytes(StandardCharsets.UTF_8));
    this.name = null;
  }

  public StringParserContext(final RfcSipMessageManager manager, final String name, final String value) {
    super(manager, value.getBytes(StandardCharsets.UTF_8));
    this.name = name;
  }

  @Override
  public String getName() {
    return this.name;
  }

}
