package io.rtcore.sip.channels.netty.tcp;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslContext;

public class SipTlsConnectionProvider implements SipConnectionProvider {

  private final EventLoopGroup eventloopGroop;
  private final SslContext sslctx;

  private SipTlsConnectionProvider(EventLoopGroup eventloopGroop, SslContext sslctx) {
    this.eventloopGroop = eventloopGroop;
    this.sslctx = sslctx;
  }

  @Override
  public SipConnection requestConnection(SipRoute route) {
    return TlsSipConnection.create(eventloopGroop, sslctx, route);
  }

  public static SipTlsConnectionProvider createProvider(EventLoopGroup elg, SslContext sslctx) {
    return new SipTlsConnectionProvider(elg, sslctx);
  }

}
