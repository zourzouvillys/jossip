package com.jive.sip.processor.rfc3261;

import com.jive.sip.message.api.SipHeaderDefinition;

public interface SipMessageManagerListener {

  SipHeaderDefinition<?> unknownHeader(final String headerName);

}
