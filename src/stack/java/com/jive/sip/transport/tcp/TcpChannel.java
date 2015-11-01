package com.jive.sip.transport.tcp;

import com.jive.sip.message.api.SipMessage;

public interface TcpChannel
{

  TcpFlowId getFlowId();

  void writeAndFlush(final SipMessage msg);

}
