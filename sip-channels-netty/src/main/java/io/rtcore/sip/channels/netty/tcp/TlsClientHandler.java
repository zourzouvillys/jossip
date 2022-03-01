package io.rtcore.sip.channels.netty.tcp;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Consumer;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.rtcore.sip.channels.netty.codec.SipCodec;
import io.rtcore.sip.channels.netty.codec.SipFrame;

class TlsClientHandler extends ChannelInitializer<NioSocketChannel> {

  private final SipRoute route;
  private final SslContext sslctx;
  private final Consumer<SipFrame> in;

  public TlsClientHandler(SipRoute route, SslContext sslctx, Consumer<SipFrame> in) {
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

    final SslHandler handler = createHandler(ch.alloc());

    // enable endpoint identification.
    SSLEngine sslEngine = handler.engine();
    SSLParameters sslParameters = sslEngine.getSSLParameters();
    sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
    sslParameters.setServerNames(List.copyOf(this.route.remoteServerNames()));
    sslEngine.setSSLParameters(sslParameters);

    // the TLS handler.
    p.addLast(handler);

    //
    p.addLast(new IdleStateHandler(0, 5, 0));
    p.addLast(new SipCodec());
    p.addLast(new SipKeepaliveHandler());

    //
    p.addLast(new RxHandler(in));

  }

  private SslHandler createHandler(ByteBufAllocator alloc) {
    return route.remoteAuthority()
      .map(remoteName -> sslctx.newHandler(alloc, remoteName.toUriString(), route.remoteAddress().getPort()))
      .orElseGet(() -> sslctx.newHandler(alloc));
  }

}
