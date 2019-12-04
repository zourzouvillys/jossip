package com.jive.sip.dummer.txn;

import com.jive.sip.message.api.SipRequest;
import com.jive.sip.transport.api.FlowId;

/**
 * Provides a mechanism for reliably sending SIP messages according to the rules in RFC 3261.
 * 
 * @author theo
 * 
 */

public interface SipSender
{

  public SipClientTransaction send(final SipRequest req, final FlowId flow, final ClientTransactionListener listener);

}
