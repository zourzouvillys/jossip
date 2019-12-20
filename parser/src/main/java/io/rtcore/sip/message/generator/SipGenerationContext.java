package io.rtcore.sip.message.generator;

import io.rtcore.sip.message.processor.rfc3261.SipMessageManager;

/**
 * 
 */

public class SipGenerationContext {

  private SipMessageManager mmgr;

  public SipGenerationContext(SipMessageManager mmgr) {
    this.mmgr = mmgr;
  }

}
