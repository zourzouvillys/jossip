package com.jive.sip.message.api;

import java.util.Optional;

public class SipProtocol {
  public static SipProtocol UDP = new SipProtocol("UDP", 5060);
  public static SipProtocol TCP = new SipProtocol("TCP", 5060);
  public static SipProtocol TLS = new SipProtocol("TLS", 5061);
  private final String protocol;
  private final Integer defaultPort;

  private SipProtocol(final String protocol, final Integer port) {
    this.protocol = protocol.toUpperCase();
    this.defaultPort = port;
  }

  public static SipProtocol fromString(final String transport) {
    if ("UDP".equalsIgnoreCase(transport)) {
      return UDP;
    } else if ("TCP".equalsIgnoreCase(transport)) {
      return TCP;
    } else if ("TLS".equalsIgnoreCase(transport)) {
      return TLS;
    } else {
      return new SipProtocol(transport, null);
    }
  }

  public Optional<Integer> getDefaultPort() {
    return Optional.ofNullable(this.defaultPort);
  }

  public String protocol() {
    return this.protocol;
  }
}
