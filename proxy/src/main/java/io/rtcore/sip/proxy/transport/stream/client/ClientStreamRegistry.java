package io.rtcore.sip.proxy.transport.stream.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import io.netty.channel.Channel;
import io.rtcore.sip.message.base.api.RawMessage;
import io.rtcore.sip.proxy.transport.stream.SipStreamChannelHandler.SipChannelActiveEvent;
import io.rtcore.sip.proxy.transport.stream.SipStreamChannelHandler.SipChannelRemovedEvent;

public class ClientStreamRegistry {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ClientStreamRegistry.class);
  private final EventBus bus;
  private Set<Channel> channels = new HashSet<>();
  private Map<String, Flow> flows = new HashMap<>();

  public ClientStreamRegistry(EventBus bus) {
    this.bus = bus;
    bus.register(this);
  }

  @Subscribe
  private void onChannelReady(SipChannelActiveEvent e) {
    Flow flow = new Flow(e);
    channels.add(e.channel());
    this.flows.put(e.open().clientToken().orElse(e.channel().id().asLongText()), flow);
    log.info("SIP channel added: {} (now {} channels)", e.channel(), channels.size());
  }

  @Subscribe
  private void onChannelRemoved(SipChannelRemovedEvent e) {
    if (!channels.remove(e.channel())) {
      log.warn("couldn't find SIP channel to remove");
      return;
    }
    log.info("SIP channel removed: {} ({} channels remain)", e.channel(), channels.size());
  }

  public Collection<Flow> flows() {
    return ImmutableList.copyOf(this.flows.values());
  }

  public boolean txmit(String flowId, RawMessage raw) {
    log.info("txmit for {}: {}", flowId, raw.getInitialLine());
    Flow flow = this.flows.get(flowId);
    if (flow == null) {
      return false;
    }
    flow.txmit(raw);
    return true;
  }

  public Flow get(String flowId) {
    return this.flows.get(flowId);
  }

}
