package com.jive.sip.transport.api;

import java.net.SocketAddress;

import com.jive.sip.message.api.SipResponse;

/**
 * Event raised when a SIP response was received over the network.
 * 
 * @author theo
 * 
 */
public final class SipResponseReceivedEvent extends SipMessageReceivedEvent
{

  private final SipResponse res;

  public SipResponseReceivedEvent(final FlowId flowId, final SocketAddress source, final SipResponse res)
  {
    super(flowId, source);
    this.res = res;
  }

  @Override
  public SipResponse getMessage()
  {
    return this.res;
  }

}
