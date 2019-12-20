package io.rtcore.sip.message.processor.rfc3261;

import io.rtcore.sip.message.message.api.SipHeaderDefinition;

public class SipMessageManagerListenerAdapter implements SipMessageManagerListener {

  @Override
  public SipHeaderDefinition<?> unknownHeader(final String headerName) {
    return null;
  }

}
