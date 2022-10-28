package io.rtcore.sip.channels.proxy;

import io.rtcore.resolver.dns.DnsClient;
import io.rtcore.sip.channels.api.SipChannel;
import io.rtcore.sip.channels.api.SipClientExchange;
import io.rtcore.sip.channels.api.SipRequestFrame;

/**
 * a channel which resolves the next hop and opens a connection to it. all exchanges are then sent
 * to this channel.
 * 
 */

public class ResolvingSipChannel implements SipChannel {

  private final DnsClient resolver;

  ResolvingSipChannel(DnsClient resolver) {
    this.resolver = resolver;
  }

  @Override
  public SipClientExchange exchange(SipRequestFrame req) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipChannel.exchange invoked.");
  }

  /**
   * 
   */

  public static ResolvingSipChannel withResolver(DnsClient resolver) {
    return new ResolvingSipChannel(resolver);
  }

}
