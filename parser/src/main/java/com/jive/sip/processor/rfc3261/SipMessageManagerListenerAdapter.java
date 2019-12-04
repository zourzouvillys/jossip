package com.jive.sip.processor.rfc3261;

import com.jive.sip.message.api.SipHeaderDefinition;


public class SipMessageManagerListenerAdapter implements SipMessageManagerListener
{

  @Override
  public SipHeaderDefinition<?> unknownHeader(final String headerName)
  {
    return null;
  }

}
