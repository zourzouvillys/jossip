package com.jive.sip.transport.api;

import java.net.SocketAddress;

import com.jive.sip.message.api.SipRequest;

/**
 * Event raised when a SIP request was received.
 * 
 * @author theo
 * 
 */
public final class SipRequestReceivedEvent extends SipMessageReceivedEvent
{

  private final SipRequest req;

  public SipRequestReceivedEvent(final FlowId flowId, final SocketAddress source, final SipRequest req)
  {
    super(flowId, source);
    this.req = req;
  }

  @Override
  public SipRequest getMessage()
  {
    return this.req;
  }

}
