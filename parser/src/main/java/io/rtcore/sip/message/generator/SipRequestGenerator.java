package io.rtcore.sip.message.generator;

import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.processor.rfc3261.SipMessageManager;

@FunctionalInterface
public interface SipRequestGenerator {

  SipRequest generate(SipMessageManager mgr);

}
