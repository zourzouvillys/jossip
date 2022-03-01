package io.rtcore.sip.message.message.api;

import java.util.OptionalInt;

import io.rtcore.sip.message.base.api.Token;

/**
 * Flyweight value object for a Transport token.
 */

public final class SipTransport extends Token {

  public static SipTransport UDP = new SipTransport(Token.from("UDP"), 5060);
  public static SipTransport TCP = new SipTransport(Token.from("TCP"), 5060);
  public static SipTransport TLS = new SipTransport(Token.from("TLS"), 5061);
  public static SipTransport WS = new SipTransport(Token.from("WS"), 80);
  public static SipTransport WSS = new SipTransport(Token.from("WSS"), 443);
  public static SipTransport DTLS = new SipTransport(Token.from("DTLS"), 5061);

  private final OptionalInt defaultPort;

  private SipTransport(final Token protocol) {
    super(protocol);
    this.defaultPort = OptionalInt.empty();
  }

  private SipTransport(final Token protocol, final int defaultPort) {
    super(protocol);
    this.defaultPort = OptionalInt.of(defaultPort);
  }

  public static SipTransport fromToken(final Token transport) {
    return new SipTransport(transport);
  }

  public static SipTransport fromString(final CharSequence transport) {
    if ("UDP".equalsIgnoreCase(transport.toString())) {
      return UDP;
    }
    if ("TCP".equalsIgnoreCase(transport.toString())) {
      return TCP;
    }
    if ("TLS".equalsIgnoreCase(transport.toString())) {
      return TLS;
    }
    else if ("WS".equalsIgnoreCase(transport.toString())) {
      return WS;
    }
    else if ("WSS".equalsIgnoreCase(transport.toString())) {
      return WSS;
    }
    else if ("DTLS".equalsIgnoreCase(transport.toString())) {
      return DTLS;
    }
    return new SipTransport(Token.from(transport));
  }

  public static OptionalInt defaultPort(final SipTransport transport) {
    return transport.defaultPort;
  }

}
