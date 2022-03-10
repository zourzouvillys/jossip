package io.rtcore.sip.channels.interceptors;

import io.rtcore.sip.channels.api.SipChannel;
import io.rtcore.sip.channels.api.SipClientExchange;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.internal.SipCallOptions;

public interface SipClientInterceptor {

  SipClientExchange interceptCall(SipRequestFrame request, SipCallOptions callOptions, SipChannel next);

}
