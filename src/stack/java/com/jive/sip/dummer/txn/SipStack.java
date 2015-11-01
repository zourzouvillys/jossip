package com.jive.sip.dummer.txn;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executor;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.api.SipMessage;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.message.api.SipResponseStatus;
import com.jive.sip.processor.rfc3261.SipMessageManager;
import com.jive.sip.processor.rfc3261.message.api.ResponseBuilder;
import com.jive.sip.transport.api.FlowId;
import com.jive.sip.transport.udp.DispatchingEventListener;
import com.jive.sip.transport.udp.ListenerId;
import com.jive.sip.transport.udp.UdpFlowId;
import com.jive.sip.transport.udp.UdpTransportManager;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * Lower layer SIP stack that exposes a transaction based interface for sending and receiving SIP
 * transactions.
 *
 * A stack can have multiple Transaction Users - both stateful and stateless.
 *
 * @author theo
 *
 */

@Slf4j
public class SipStack extends AbstractService implements SipSender
{

  private final InMemoryTransactionManager txnmgr;
  private final UdpTransportManager transport;
  private int lid = 0;
  private final Map<SipMethod, NonInviteServerTransactionHandler> listeners = Maps.newLinkedHashMap();
  final SipMessageManager messages;
  private InviteServerTransactionHandler invite;
  private final HostAndPort self;
  private final TransactionUserSelector selector;

  private final NioEventLoopGroup group;
  private String serverName;

  private final BranchGenerator gen = new BranchGenerator("1");
  private SipMessageHandler handler;

  private NonInviteServerTransactionHandler allmethods;

  /**
   * Map listener ids to addr + port.
   */

  private final Map<ListenerId, InetSocketAddress> locals = Maps.newHashMap();

  /**
   *
   * @param messages
   *          The SIP message manager.
   * @param self
   *          The address which should be advertised to peers.
   */

  public SipStack(final SipMessageManager messages, final HostAndPort self,
       final String id)
  {
    this(messages, self, id, new NioEventLoopGroup(1, new ThreadFactoryBuilder().setNameFormat(
        String.format("%s-%%s", id)).build()));
  }

  public SipStack(final SipMessageManager messages, final HostAndPort self,
      final String id,
      final NioEventLoopGroup group)
  {

    this.group = group;

    this.transport = new UdpTransportManager(this.group);

    this.txnmgr = new InMemoryTransactionManager(this, this.transport, this.group, this.gen);

    this.selector = new TransactionUserSelector(this, this.txnmgr, this.gen);

    // messages get dispatches into the transaction executor.
    this.transport.addListener(this.selector, this.txnmgr.getExecutor());

    // this.tcp = new TcpTransportManager();
    // this.tcp.addListener(this.txnmgr, this.txnmgr.getExecutor());

    this.messages = messages;

    this.self = self;

    this.transport.addListener(new Listener()
    {

      @Override
      public void running()
      {
        SipStack.this.notifyStarted();
      }

      @Override
      public void terminated(final State from)
      {
        log.info("Stack terminated");
        SipStack.this.notifyStopped();
      }

      @Override
      public void failed(final State from, final Throwable failure)
      {
        SipStack.this.notifyFailed(failure);
      }

    }, MoreExecutors.sameThreadExecutor());

  }

  public ListenerId addListener(final InetSocketAddress addr)
  {
    final ListenerId lid = new ListenerId(this.lid++);
    final InetSocketAddress local = this.transport.addListener(lid, addr);
    this.locals.put(lid, local);
    return lid;
  }

  public InetSocketAddress getLocal(final ListenerId lid)
  {
    return this.locals.get(lid);
  }

  public ListenerId addListener(final HostAndPort hp)
  {
    return this.addListener(new InetSocketAddress(InetAddresses.forString(hp.getHostText()), hp.getPortOrDefault(0)));
  }

  public ListenerId addListener(final int port)
  {
    return this.addListener(new InetSocketAddress(port));
  }

  @Override
  public SipClientTransaction send(final SipRequest req, final FlowId flow, final ClientTransactionListener listener)
  {
    return this.txnmgr.fromApplication(req, flow, listener, null);
  }

  public SipClientTransaction send(final SipRequest req, final FlowId flow, final ClientTransactionListener listener,
      final ClientTransactionOptions ops)
  {
    return this.txnmgr.fromApplication(req, flow, listener, ops);
  }

  public void addNonInviteHandler(final SipMethod method, final NonInviteServerTransactionHandler listener,
      final Executor executor)
  {
    final DispatchingEventListener<NonInviteServerTransactionHandler> ssm =
        DispatchingEventListener.create(NonInviteServerTransactionHandler.class);
    ssm.add(listener, executor);
    this.listeners.put(method, ssm.getInvoker());
  }

  public void addNonInviteHandler(final NonInviteServerTransactionHandler listener, final Executor executor)
  {
    final DispatchingEventListener<NonInviteServerTransactionHandler> ssm =
        DispatchingEventListener.create(NonInviteServerTransactionHandler.class);
    ssm.add(listener, executor);
    this.allmethods = ssm.getInvoker();
  }

  public void addInviteHandler(final InviteServerTransactionHandler handler, final Executor executor)
  {
    this.addInviteHandler(handler, executor, true);
  }

  public void addInviteHandler(final InviteServerTransactionHandler handler, final Executor executor, final boolean indialog)
  {

    final DispatchingEventListener<InviteServerTransactionHandler> ssm =
        DispatchingEventListener.create(InviteServerTransactionHandler.class);

    ssm.add(handler, executor);

    this.invite = ssm.getInvoker();
    if (indialog)
    {
      this.listeners.put(SipMethod.ACK, ssm.getInvoker());
      this.listeners.put(SipMethod.BYE, ssm.getInvoker());
      this.listeners.put(SipMethod.CANCEL, ssm.getInvoker());
      this.listeners.put(SipMethod.INFO, ssm.getInvoker());
      this.listeners.put(SipMethod.MESSAGE, ssm.getInvoker());
      this.listeners.put(SipMethod.REFER, ssm.getInvoker());
      this.listeners.put(SipMethod.PRACK, ssm.getInvoker());
      this.listeners.put(SipMethod.UPDATE, ssm.getInvoker());
    }
  }

  public NonInviteServerTransactionHandler getHandler(final SipMethod method)
  {

    final NonInviteServerTransactionHandler handler = this.listeners.get(method);

    if (handler == null)
    {
      return this.allmethods;
    }

    return handler;

  }

  public SipResponse createResponse(final SipRequest req, final SipResponseStatus status)
  {
    ResponseBuilder builder = this.messages.responseBuilder(status);
    if (this.serverName != null)
    {
      builder = builder.addHeader(new RawHeader("Server", this.serverName));
    }
    return builder.build(req);
  }

  public void setStatelessHandler(final SipMessageHandler handler)
  {
    log.info("Setting stateless handler for {} {}", this, handler);
    this.handler = handler;
  }

  public SipMessageHandler getStatelessHandler()
  {
    return this.handler;
  }

  /**
   * Used for sending the 2xx ACK directly.
   *
   * @param req
   * @param flowId
   */

  public void sendAck(final SipRequest req, final UdpFlowId flowId)
  {
    Preconditions.checkArgument(req.getMethod().equals(SipMethod.ACK));
    this.transport.send(flowId, req);
  }

  public void sendTransport(final SipMessage msg, final FlowId flowId)
  {
    if (flowId instanceof UdpFlowId)
    {
      this.transport.send((UdpFlowId) flowId, msg);
    }
    else
    {
      throw new RuntimeException("Only UDP currently supported");
    }
  }

  public InviteServerTransactionHandler getInviteServerHandler()
  {
    return this.invite;
  }

  public SipMessageManager getMessageManager()
  {
    return this.messages;
  }

  public HostAndPort getSelf()
  {
    return this.self;
  }

  @Override
  protected void doStart()
  {
    log.info("Starting stack");
    this.transport.startAsync();
  }

  @Override
  protected void doStop()
  {
    log.info("Stopping stack");
    this.transport.stopAsync();
  }

  public void setServerName(final String name)
  {
    this.serverName = name;
  }

  public String getServerName()
  {
    return this.serverName;
  }

}
