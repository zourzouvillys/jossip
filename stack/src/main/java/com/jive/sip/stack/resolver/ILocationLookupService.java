package com.jive.sip.stack.resolver;

import java.util.List;

import com.jive.sip.message.api.SipTransport;
import com.jive.sip.uri.api.SipUri;

public interface ILocationLookupService
{

  public static final TransportProtocol TLS = new TransportProtocol(SipTransport.TCP, "SIPS+D2T", 5061, true);
  public static final TransportProtocol TCP = new TransportProtocol(SipTransport.TCP, "SIP+D2T", 5060, false);
  public static final TransportProtocol UDP = new TransportProtocol(SipTransport.UDP, "SIP+D2U", 5060, false);

  /**
   * 
   * @param input
   *          The input string, e.g "sip:theo@awesip.com"
   * @param services
   *          The services that should be queried, in the order they should be selected, e.g "SIPS+D2T", "SIP+D2T", "SIP+D2U"
   * @return A list of endpoints to try
   * 
   *         TODO: add "result" support, which allows us to provide a list of hosts we've already tried but failed, and more be returned.
   * 
   */

  List<ServiceResult> lookup(final SipUri input, final TransportProtocol[] supported);

}
