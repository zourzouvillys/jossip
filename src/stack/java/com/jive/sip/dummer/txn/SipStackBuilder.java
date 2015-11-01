package com.jive.sip.dummer.txn;

import java.util.Map;
import java.util.concurrent.Executor;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.processor.rfc3261.RfcSipMessageManagerBuilder;
import com.jive.sip.processor.rfc3261.SipMessageManager;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Value;

/**
 * Helper to create a new {@link SipStack}
 * 
 * <code>
 * {@link SipStack} stack = new {@link SipStackBuilder}(HostAndPort.fromParts("10.0.0.1", 5060))
 *   .withInviteHandler(myHandler)
 *   .withNonInviteHandler(SipMethod.REGISTER, registerHandler, myExecutor)
 *   .build();
 * </code>
 * 
 * 
 * @author theo
 * 
 */
public class SipStackBuilder
{

  private final HostAndPort self;
  private final SipMessageManager messages = new RfcSipMessageManagerBuilder().build();
  private String id;
  private Map<SipMethod, Handler<NonInviteServerTransactionHandler>> handlers = Maps.newHashMap();
  private Handler<InviteServerTransactionHandler> inviteHandler = null;
  private Handler<NonInviteServerTransactionHandler> standardHandler = null;
  private SipMessageHandler statelessHandler;
  private int threads = 1;
  private NioEventLoopGroup group = null;

  @Value
  private static class Handler<T>
  {
    SipMethod method;
    T handler;
    Executor executor;
  }

  public SipStackBuilder(final String self)
  {
    this.self = HostAndPort.fromString(self);
  }

  public SipStackBuilder(final HostAndPort self)
  {
    this.self = self;
  }

  public SipStackBuilder withId(final String id)
  {
    this.id = id;
    return this;
  }

  public SipStackBuilder withThreadCount(int count)
  {
    this.threads = count;
    return this;
  }

  public SipStackBuilder withThreadCount(NioEventLoopGroup group)
  {
    this.group = group;
    return this;
  }

  public SipStackBuilder withInviteHandler(InviteServerTransactionHandler handler, Executor executor)
  {
    Preconditions.checkState(this.inviteHandler == null);
    this.inviteHandler = new Handler<InviteServerTransactionHandler>(SipMethod.INVITE, handler, executor);
    return this;
  }

  public SipStackBuilder withNonInviteHandler(SipMethod method, NonInviteServerTransactionHandler handler, Executor executor)
  {
    Preconditions.checkState(handlers.get(method) == null);
    this.handlers.put(method, new Handler<NonInviteServerTransactionHandler>(method, handler, executor));
    return this;
  }

  public SipStackBuilder withNonInviteHandler(NonInviteServerTransactionHandler handler, Executor executor)
  {
    Preconditions.checkState(this.standardHandler == null);
    this.standardHandler = new Handler<NonInviteServerTransactionHandler>(null, handler, executor);
    return this;
  }

  public SipStackBuilder withStatelessHandler(SipMessageHandler handler)
  {
    this.statelessHandler = handler;
    return this;
  }

  public SipStack build()
  {

    if (this.id == null)
    {
      this.id = "unnamed";
    }

    if (this.group == null)
    {
      this.group = new NioEventLoopGroup(this.threads, new ThreadFactoryBuilder().setNameFormat(String.format("%s-%%s", id)).build());
    }

    SipStack stack = new SipStack(this.messages, this.self, this.id, group);

    if (this.inviteHandler != null)
    {
      stack.addInviteHandler(inviteHandler.getHandler(), inviteHandler.getExecutor());
    }

    for (Handler<NonInviteServerTransactionHandler> handler : this.handlers.values())
    {
      stack.addNonInviteHandler(handler.getMethod(), handler.getHandler(), handler.getExecutor());
    }

    if (this.standardHandler != null)
    {
      stack.addNonInviteHandler(this.standardHandler.getHandler(), this.standardHandler.getExecutor());
    }

    if (this.statelessHandler != null)
    {
      stack.setStatelessHandler(statelessHandler);
    }

    return stack;

  }

}
