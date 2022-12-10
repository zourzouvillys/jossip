package io.rtcore.gateway.engine;

import io.netty.channel.nio.NioEventLoopGroup;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.connection.SipRoute;

public class SipEngine {

  private final SipSegment segment;
  private final NioEventLoopGroup eventLoopGroup;

  SipEngine(final SipRoute route) {
    this.eventLoopGroup = new NioEventLoopGroup(1);
    this.segment = new SipSegment(this.eventLoopGroup, route);
  }

  public void send(final SipRequestFrame req, final OutgoingRequestDelegate delegate) {
    this.segment.send(req, delegate);
  }

}
