package io.rtcore.sip.channels.netty.tcp;

import io.netty.channel.EventLoopGroup;
import io.rtcore.sip.channels.connection.SipConnection;
import io.rtcore.sip.channels.connection.SipConnectionProvider;
import io.rtcore.sip.channels.connection.SipRoute;

public class SipTlsConnectionProvider implements SipConnectionProvider {

  private final EventLoopGroup eventloopGroop;
  private final TlsContextProvider sslctx;

  private SipTlsConnectionProvider(EventLoopGroup eventloopGroop, TlsContextProvider sslctx) {
    this.eventloopGroop = eventloopGroop;
    this.sslctx = sslctx;
  }

  @Override
  public SipConnection requestConnection(SipRoute route) {
    return TlsSipConnection.create(eventloopGroop, sslctx, route);
  }

  public static SipTlsConnectionProvider createProvider(EventLoopGroup elg, TlsContextProvider sslctx) {
    return new SipTlsConnectionProvider(elg, sslctx);
  }

}
