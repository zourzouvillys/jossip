package io.rtcore.sip.channels.netty.tcp;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslContext;
import io.rtcore.sip.channels.connection.SipConnection;
import io.rtcore.sip.channels.connection.SipConnectionProvider;
import io.rtcore.sip.channels.connection.SipRoute;

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
