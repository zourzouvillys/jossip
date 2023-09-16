package io.rtcore.sip.channels.interceptors;

import io.rtcore.sip.channels.api.SipCallOptions;
import io.rtcore.sip.channels.api.SipChannel;
import io.rtcore.sip.channels.api.SipClientExchange;
import io.rtcore.sip.frame.SipRequestFrame;

public interface SipClientInterceptor {

  SipClientExchange interceptCall(
      SipRequestFrame request,
      SipCallOptions callOptions,
      SipChannel next);

}
