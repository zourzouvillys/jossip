package io.rtcore.sip.message.message.api;

import io.rtcore.sip.message.base.api.Token;

/**
 * Flyweight value object for a Transport token.
 */

public final class SipTransport extends Token {

  public static SipTransport UDP = new SipTransport(Token.from("UDP"));
  public static SipTransport TCP = new SipTransport(Token.from("TCP"));
  public static SipTransport TLS = new SipTransport(Token.from("TLS"));
  public static SipTransport WS = new SipTransport(Token.from("WS"));
  public static SipTransport WSS = new SipTransport(Token.from("WSS"));
  public static SipTransport DTLS = new SipTransport(Token.from("DTLS"));

  private SipTransport(final Token protocol) {
    super(protocol);
  }

  public static SipTransport fromToken(final Token transport) {
    return new SipTransport(transport);
  }

  public static SipTransport fromString(final CharSequence transport) {
    if ("UDP".equalsIgnoreCase(transport.toString())) {
      return UDP;
    }
    else if ("TCP".equalsIgnoreCase(transport.toString())) {
      return TCP;
    }
    else if ("TLS".equalsIgnoreCase(transport.toString())) {
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

}
