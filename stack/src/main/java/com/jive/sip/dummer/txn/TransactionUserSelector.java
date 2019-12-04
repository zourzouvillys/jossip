package com.jive.sip.dummer.txn;

import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Preconditions;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.message.api.SipResponseStatus;
import com.jive.sip.message.api.Via;
import com.jive.sip.processor.rfc3261.MutableSipResponse;
import com.jive.sip.transport.udp.UdpFlowId;
import com.jive.sip.transport.udp.UdpTransportListener;

/**
 * Looks at the message to decide where to send it.
 * 
 * This is to allow a client to use a socket/transport as both a stateful and stateless transaction user, e.g sending
 * REGISTER statefully, but then processing incoming responses statelessly.
 * 
 * @author theo
 * 
 */

@Slf4j
public class TransactionUserSelector implements UdpTransportListener
{

  private final InMemoryTransactionManager txnmgr;
  private final SipStack stack;
  private final BranchGenerator branchGenerator;

  public TransactionUserSelector(final SipStack stack, final InMemoryTransactionManager txnmgr, final BranchGenerator gen)
  {
    this.stack = stack;
    this.txnmgr = Preconditions.checkNotNull(txnmgr);
    this.branchGenerator = gen;
  }

  @Override
  public void onSipRequestReceived(final UdpFlowId flow, final InetSocketAddress sender, final SipRequest msg)
  {

    // we need a way of selecting - check to see if a transaction handler for the given method has been added, perhaps?

    final SipMethod method = msg.getMethod();

    if ((method.equals(SipMethod.INVITE) || method.equals(SipMethod.ACK)) && (this.stack.getInviteServerHandler() != null))
    {
      this.txnmgr.onSipRequestReceived(flow, sender, msg);
    }
    else if (this.stack.getHandler(method) != null)
    {
      this.txnmgr.onSipRequestReceived(flow, sender, msg);
    }
    else
    {

      final SipMessageHandler handler = this.stack.getStatelessHandler();

      // otherwise pass to stateless handler. it can always feed directly into the txn handler if it wants to.

      if (handler == null)
      {
        log.info("Rejecting request from {} without handler {}", flow, msg);
        if (!method.equals(SipMethod.ACK))
        {
          // no handler - reject.
          final MutableSipResponse res = MutableSipResponse.createResponse(msg, SipResponseStatus.METHOD_NOT_ALLOWED);
          res.server("Jive FTW Stack (TU)");
          res.add("X-JTraceInfo", "A01");
          this.stack.sendTransport(res.build(this.stack.getMessageManager()), flow);
        }
      }
      else
      {
        handler.processRequest(msg, flow);
      }

    }

  }

  @Override
  public void onSipResponseReceived(final UdpFlowId flow, final InetSocketAddress sender, final SipResponse msg)
  {

    final Via v = msg.getVias().iterator().next();

    if (v.getSentBy().equals(this.txnmgr.getSelf(flow)))
    {

      if (this.branchGenerator.isMine(msg.getBranchId().getValueWithoutCookie()))
      {
        this.txnmgr.onSipResponseReceived(flow, sender, msg);
        return;
      }

    }

    // it either wasn't our top via header, or not a local transaction user. Pass up to stateless handler.

    final SipMessageHandler handler = this.stack.getStatelessHandler();

    if (handler != null)
    {
      handler.processResponse(msg, flow);
      return;
    }

    // no handler, and an unknown response. drop.

    log.info("Dropping response from {} on the floor: {}", flow, msg);

  }

  @Override
  public void onInvalidSipMessageEvent(final UdpFlowId createFlowId, final InetSocketAddress sender)
  {
    this.txnmgr.onInvalidSipMessageEvent(createFlowId, sender);
  }

  @Override
  public void onKeepalive(final UdpFlowId flow, final InetSocketAddress sender)
  {
    this.txnmgr.onKeepalive(flow, sender);
  }

  @Override
  public void onStunPacket(final UdpFlowId flow, final DatagramPacket pkt)
  {
    this.txnmgr.onStunPacket(flow, pkt);
  }

}
