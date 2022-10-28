package io.rtcore.sip.channels.netty.tcp;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.immutables.value.Value;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslContext;
import io.rtcore.sip.channels.api.SipFrameUtils;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.api.SipResponseFrame;
import io.rtcore.sip.channels.api.SipServerExchangeHandler;
import io.rtcore.sip.channels.api.SipServerExchangeInterceptor;
import io.rtcore.sip.common.iana.SipStatusCodes;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class }, depluralize = true)
public interface NettySocketServerConfig {

  /**
   * 
   */

  EventLoopGroup acceptGroup();

  /**
   * 
   */

  EventLoopGroup childGroup();

  /**
   * 
   */

  Optional<SslContext> sslctx();

  /**
   * address to listen on.
   */

  @Value.Default
  default InetSocketAddress listenAddress() {
    return new InetSocketAddress(0);
  }

  /**
   * handler which will process incoming exchanges.
   */

  @Value.Default
  default SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> serverHandler() {
    return (exchange, attributes) -> {
      exchange.onNext(SipFrameUtils.createResponse(exchange.request(), SipStatusCodes.OK));
      exchange.onComplete();
      return null;
    };
  }

  /**
   * interceptors to apply to incoming exchanges.
   */

  List<SipServerExchangeInterceptor<SipRequestFrame, SipResponseFrame>> interceptors();

  /**
   * 
   */

  @Value.Default
  default TcpConnectionConfig tcpConfig() {
    return ImmutableTcpConnectionConfig.builder().build();
  }

  /**
   * create a new builder.
   */

  public static ImmutableNettySocketServerConfig.Builder builder() {
    return ImmutableNettySocketServerConfig.builder();
  }

  /**
   * create a new instance by applying the builder.
   */

  public static ImmutableNettySocketServerConfig create(UnaryOperator<ImmutableNettySocketServerConfig.Builder> b) {
    return b.apply(builder()).build();
  }

  /**
   * default builder.
   */

  public static ImmutableNettySocketServerConfig createDefault() {
    return builder().build();
  }

}
