package io.rtcore.sip.channels.netty.tcp;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.rtcore.sip.channels.connection.SipRoute;

public class StaticTlsContextProvider implements TlsContextProvider {

  private SslContext sslctx;

  StaticTlsContextProvider(SslContext sslctx) {
    this.sslctx = sslctx;
  }

  public SslHandler newHandler(ByteBufAllocator alloc, SipRoute route) {
    return route.remoteAuthority()
      .map(remoteName -> sslctx.newHandler(alloc, remoteName.toUriString(), route.remoteAddress().getPort()))
      .orElseGet(() -> sslctx.newHandler(alloc));
  }

  public static StaticTlsContextProvider of(SslContext sslctx) {
    return new StaticTlsContextProvider(sslctx);
  }

}
