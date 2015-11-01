package com.jive.sip.transport.tcp;

import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;

public interface TcpTransportListener
{

  void onSipRequestReceived(final TcpFlowId flow, final SipRequest msg);

  void onSipResponseReceived(final TcpFlowId flow, final SipResponse msg);

  void onInvalidSipMessageEvent(final TcpFlowId createFlowId);

  void onKeepalive(final TcpFlowId flow);

  void onClosed();

}
