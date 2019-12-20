package io.rtcore.sip.message.base.api;

import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

/**
 * Immutable flyweight object representing a token in the SIP protocol.
 *
 * Note that although SIP method is a token in the RFC 3261 BNF, it is case sensitive, so we don't
 * treat it as one. Use SipMethod instead.
 *
 * TODO: add a cache for common tokens.
 *
 * 
 *
 */

public class Token {

  public static final Token TRUE = Token.from("true");
  public static final Token FALSE = Token.from("false");
  public static final Token ZERO = Token.from("0");
  public static final Token ONE = Token.from("1");

  private final String value;

  protected Token(final CharSequence value) {
    this.value = Preconditions.checkNotNull(value.toString());
  }

  protected Token(final Token value) {
    this.value = Preconditions.checkNotNull(value.toString());
  }

  public static Token from(final CharSequence token) {
    Preconditions.checkNotNull(token);
    final String str = token.toString();
    Preconditions.checkNotNull(str);
    Preconditions.checkArgument(!str.isEmpty());
    Preconditions.checkArgument(Pattern.matches("[-A-Za-z0-9.!%*_+`'~]+", str), str);
    return new Token(str);
  }

  public static Token from(final long value) {
    return from(Long.toString(value));
  }

  @Override
  public boolean equals(final Object other) {
    if (other instanceof Token) {
      return ((Token) other).value.equalsIgnoreCase(this.value);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.value.toLowerCase().hashCode();
  }

  @Override
  public String toString() {
    return this.value;
  }

}
