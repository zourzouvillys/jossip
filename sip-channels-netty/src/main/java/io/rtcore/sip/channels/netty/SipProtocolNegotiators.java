package io.rtcore.sip.channels.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.rtcore.sip.channels.auth.SipChannelCredentials;
import io.rtcore.sip.channels.auth.TlsChannelCredentials;
import io.rtcore.sip.netty.codec.SipStreamCodec;

public final class SipProtocolNegotiators {

  private static final int DEFAULT_SIP_TLS_PORT = 5061;

  private SipProtocolNegotiators() {
  }

  public static SipProtocolNegotiator.ClientFactory forClient(final SipChannelCredentials creds) {
    if (creds instanceof final TlsChannelCredentials tls) {
      return new TlsClientNegotiatorFactory(tls);
    }
    throw new IllegalArgumentException(creds.getClass().getName());
  }

  private final static class TlsClientNegotiatorFactory implements SipProtocolNegotiator.ClientFactory, SipProtocolNegotiator {

    public TlsClientNegotiatorFactory(final TlsChannelCredentials tls) {
    }

    @Override
    public SipProtocolNegotiator newNegotiator() {
      return this;
    }

    @Override
    public int getDefaultPort() {
      return DEFAULT_SIP_TLS_PORT;
    }

    @Override
    public ChannelHandler newHandler() {
      throw new IllegalArgumentException("todo");
    }

  }

  // ----

  /**
   * 
   */

  public static SipProtocolNegotiator.ServerFactory forServer(final SipChannelCredentials creds) {
    if (creds instanceof final TlsChannelCredentials tls) {
      return new TlsServerNegotiatorFactory(tls);
    }
    throw new IllegalArgumentException(creds.getClass().getName());
  }

  public static SipProtocolNegotiator.ServerFactory forServer() {
    return new TlsServerNegotiatorFactory();
  }

  private final static class TlsServerNegotiatorFactory implements SipProtocolNegotiator.ServerFactory, SipProtocolNegotiator {

    private TlsChannelCredentials tls;

    public TlsServerNegotiatorFactory() {
    }

    public TlsServerNegotiatorFactory(final TlsChannelCredentials tls) {
      this.tls = tls;
    }

    @Override
    public SipProtocolNegotiator newNegotiator() {
      return this;
    }

    @Override
    public int getDefaultPort() {
      return this.tls == null ? 5060
                              : 5061;
    }

    @Override
    public ChannelHandler newHandler() {

      return new ChannelInitializer<Channel>() {

        @Override
        protected void initChannel(Channel ch) throws Exception {
          ChannelPipeline p = ch.pipeline();
          p.addLast(new SipStreamCodec());
        }
      };

    }

  }

}
