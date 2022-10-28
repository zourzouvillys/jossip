package io.rtcore.sip.channels.netty.tcp;

import java.net.InetSocketAddress;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.rtcore.sip.channels.api.SipFrame;
import io.rtcore.sip.channels.connection.SipRoute;
import io.rtcore.sip.channels.netty.codec.SipCodec;

class TlsClientHandler extends ChannelInitializer<NioSocketChannel> {

  private final SipRoute route;
  private final TlsContextProvider sslctx;
  private final Consumer<SipFrame> in;

  public TlsClientHandler(SipRoute route, TlsContextProvider sslctx, Consumer<SipFrame> in) {
    this.route = route;
    this.sslctx = sslctx;
    this.in = in;
  }

  @Override
  protected void initChannel(NioSocketChannel ch) throws Exception {

    ChannelPipeline p = ch.pipeline();

    for (InetSocketAddress proxy : route.proxyChain()) {
      p.addLast(new Socks5ProxyHandler(proxy));
    }

    if (this.sslctx != null) {

      final SslHandler handler = createHandler(ch.alloc());

      // enable endpoint identification.
      SSLEngine sslEngine = handler.engine();
      SSLParameters sslParameters = sslEngine.getSSLParameters();
      sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
      sslParameters.setServerNames(
        this.route.remoteServerNames()
          .stream()
          .map(name -> new SNIHostName(name))
          .collect(Collectors.toList()));
      sslEngine.setSSLParameters(sslParameters);

      // the TLS handler.
      p.addLast(handler);

    }

    //
    p.addLast(new IdleStateHandler(0, 5, 0));
    p.addLast(new SipCodec());
    p.addLast(new SipKeepaliveHandler());

    //
    p.addLast(new RxHandler(in));

  }

  private SslHandler createHandler(ByteBufAllocator alloc) {
    return sslctx.newHandler(alloc, route);
  }

}
