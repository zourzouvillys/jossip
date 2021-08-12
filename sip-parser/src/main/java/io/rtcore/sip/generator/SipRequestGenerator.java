package io.rtcore.sip.generator;

import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.processor.rfc3261.SipMessageManager;

@FunctionalInterface
public interface SipRequestGenerator extends SipMessageGenerator {

  SipRequest generate(SipMessageManager mgr);

  default SipRequest generate() {
    return generate(SipMessageManager.defaultManager());
  }

}
