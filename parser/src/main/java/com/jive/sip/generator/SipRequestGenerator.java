package com.jive.sip.generator;

import com.jive.sip.message.SipRequest;
import com.jive.sip.processor.rfc3261.SipMessageManager;

@FunctionalInterface
public interface SipRequestGenerator {

  SipRequest generate(SipMessageManager mgr);

}
