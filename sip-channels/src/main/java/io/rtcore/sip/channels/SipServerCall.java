package io.rtcore.sip.channels;

import io.rtcore.sip.message.message.SipRequest;

/**
 *
 */

public interface SipServerCall extends Attributed {

  SipRequest request();

}
