package io.rtcore.sip.proxy.transport.stream.client;

import java.util.Arrays;
import java.util.function.Function;

import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

import com.google.common.eventbus.EventBus;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.reactivex.rxjava3.subjects.SingleSubject;
import io.rtcore.sip.netty.codec.SipCodec;
import io.rtcore.sip.netty.codec.SipObjectAggregator;
import io.rtcore.sip.proxy.actions.OpenStream;
import io.rtcore.sip.proxy.transport.stream.SipStreamChannelHandler;

final class ClientConnectorFactory {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ClientConnectorFactory.class);

  public Function<EventBus, ChannelHandler> createChannelHandler(OpenStream open) {
    return eventBus -> new SipServerHandlerInit(eventBus, open);
  }

  static class SipServerHandlerInit extends ChannelInitializer<SocketChannel> {

    private EventBus eventBus;
    private OpenStream open;

    public SipServerHandlerInit(EventBus eventBus, OpenStream open) {
      this.eventBus = eventBus;
      this.open = open;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
      socketChannel
        .pipeline()
        .addLast(new FlowControlHandler())
        .addLast(new FlushConsolidationHandler(256, true))
        .addLast(new IdleStateHandler(10, 5, 0))
        // .addLast(new LoggingHandler(LogLevel.INFO))
        .addLast(new SipCodec())
        .addLast(new SipObjectAggregator(65_535))
        .addLast(new SipStreamChannelHandler(eventBus, open));
    }

  }

  static class SecureSipClientHandlerInit extends ChannelInitializer<SocketChannel> {

    private SslContext sslctx;
    private String targetHost;
    private int targetPort;
    private SingleSubject<Channel> subject;

    public SecureSipClientHandlerInit(SingleSubject<Channel> subject, SslContext sslctx, String targetHost, int targetPort) {
      this.sslctx = sslctx;
      this.subject = subject;
      this.targetHost = targetHost;
      this.targetPort = targetPort;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

      SslHandler sslHandler = sslctx.newHandler(socketChannel.alloc(), targetHost, targetPort);
      SSLEngine sslEngine = sslHandler.engine();
      SSLParameters sslParameters = sslEngine.getSSLParameters();
      sslParameters.setServerNames(Arrays.asList(new SNIHostName(targetHost)));
      sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
      sslEngine.setSSLParameters(sslParameters);

      //

      socketChannel
        .pipeline()
        .addLast(new FlowControlHandler())
        .addLast(new FlushConsolidationHandler(256, true))
        // note that idle is before TLS, that way any TLS packets are included in idle handling.
        .addLast(new IdleStateHandler(10, 5, 0))
        .addLast(sslHandler)
        // .addLast(new LoggingHandler(LogLevel.INFO))
        .addLast(new SipCodec())
        .addLast(new SipObjectAggregator(65_535))
      // .addLast(new SipStreamChannelHandler(eventBus))

      // .addLast(
      // targetHost != null
      // ? new SipStreamChannelHandler(eventBus, subject, InternetDomainName.from(targetHost))
      // : new SipStreamChannelHandler(eventBus))
      //
      ;

    }

  }

}
