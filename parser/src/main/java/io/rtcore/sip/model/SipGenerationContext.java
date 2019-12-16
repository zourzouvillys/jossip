package io.rtcore.sip.model;

import com.jive.sip.processor.rfc3261.SipMessageManager;

/**
 * 
 */

public class SipGenerationContext {

  private SipMessageManager mmgr;

  public SipGenerationContext(SipMessageManager mmgr) {
    this.mmgr = mmgr;
  }

}
