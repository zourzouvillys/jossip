package com.jive.sip.transport.api;

import java.net.SocketAddress;

import lombok.Getter;

import com.jive.sip.message.api.SipMessage;

/**
 * Event when a message is received over a transport.
 * 
 * @author theo
 * 
 */

public abstract class SipMessageReceivedEvent
{

  SipMessageReceivedEvent(final FlowId flowId, final SocketAddress source)
  {
    this.flowId = flowId;
    this.source = source;
  }

  @Getter
  private final FlowId flowId;

  @Getter
  private final SocketAddress source;

  public abstract SipMessage getMessage();

}
