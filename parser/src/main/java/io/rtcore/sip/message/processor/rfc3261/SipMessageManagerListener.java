package io.rtcore.sip.message.processor.rfc3261;

import io.rtcore.sip.message.message.api.SipHeaderDefinition;

public interface SipMessageManagerListener {

  SipHeaderDefinition<?> unknownHeader(final String headerName);

}
