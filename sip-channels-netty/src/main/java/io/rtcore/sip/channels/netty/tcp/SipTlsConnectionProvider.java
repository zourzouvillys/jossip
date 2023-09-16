package io.rtcore.sip.channels.netty.tcp;

import io.netty.channel.EventLoopGroup;
import io.rtcore.sip.channels.api.SipServerExchangeHandler;
import io.rtcore.sip.channels.connection.SipConnection;
import io.rtcore.sip.channels.connection.SipConnectionProvider;
import io.rtcore.sip.channels.connection.SipRoute;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.frame.SipResponseFrame;

public class SipTlsConnectionProvider implements SipConnectionProvider {

  private final EventLoopGroup eventloopGroop;
  private final TlsContextProvider sslctx;
  private SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> server;

  private SipTlsConnectionProvider(
      EventLoopGroup eventloopGroop,
      TlsContextProvider sslctx,
      SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> serverHandler) {

    this.eventloopGroop = eventloopGroop;
    this.sslctx = sslctx;
    this.server = serverHandler;

  }

  @Override
  public SipConnection requestConnection(SipRoute route) {
    return TlsSipConnection.create(eventloopGroop, sslctx, route, this.server);
  }

  public static
      SipTlsConnectionProvider
      createProvider(
          EventLoopGroup elg,
          TlsContextProvider sslctx,
          SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> handler) {

    return new SipTlsConnectionProvider(elg, sslctx, handler);

  }

}
