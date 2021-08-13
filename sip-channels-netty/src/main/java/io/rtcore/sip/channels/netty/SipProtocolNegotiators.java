package io.rtcore.sip.channels.netty;

import io.netty.channel.ChannelHandler;
import io.rtcore.sip.channels.SipChannelCredentials;
import io.rtcore.sip.channels.TlsChannelCredentials;

class SipProtocolNegotiators {

  private static final int DEFAULT_SIP_TLS_PORT = 5061;

  static SipProtocolNegotiator.ClientFactory forClient(final SipChannelCredentials creds) {
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

}
