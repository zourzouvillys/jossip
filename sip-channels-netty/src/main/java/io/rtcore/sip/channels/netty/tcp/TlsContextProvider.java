package io.rtcore.sip.channels.netty.tcp;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.rtcore.sip.channels.connection.SipRoute;

public interface TlsContextProvider {

  /**
   * provides a new handler for the specified remote TLS route.
   * 
   * @param alloc
   * @param route
   * @return
   */

  SslHandler newHandler(ByteBufAllocator alloc, SipRoute route);

  static TlsContextProvider of(SslContext sslctx) {
    return new StaticTlsContextProvider(sslctx);
  }

}
