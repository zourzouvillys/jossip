package com.jive.sip.message.api;

import com.jive.sip.base.api.Token;

/**
 * Flyweight value object for a Transport token.
 * 
 * @author theo
 * 
 */

public final class SipTransport extends Token {

  public static SipTransport UDP = new SipTransport(Token.from("UDP"));
  public static SipTransport TCP = new SipTransport(Token.from("TCP"));
  public static SipTransport TLS = new SipTransport(Token.from("TLS"));

  private SipTransport(final Token protocol) {
    super(protocol);
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
    return new SipTransport(Token.from(transport));
  }

}
