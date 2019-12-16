package com.jive.sip.processor.rfc3261.parsing.parsers;

import java.nio.charset.StandardCharsets;

import com.jive.sip.processor.rfc3261.SipMessageManager;
import com.jive.sip.processor.rfc3261.parsing.AbstractStringParseContext;

public class TestHeaderParseContext extends AbstractStringParseContext {

  public TestHeaderParseContext(final SipMessageManager manager, final String string) {
    super(manager, string.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public String getName() {
    return "Test";
  }

}
