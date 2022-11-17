package io.zrz.rtcore.useragent;

import java.util.List;

import io.rtcore.sip.channels.api.SipClientExchange.Event;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipMethodId;

public interface SipClientAuthProvider {

  List<SipHeaderLine> generate(SipMethodId method, String ruri, String body);

  void observe(Event res);

}
