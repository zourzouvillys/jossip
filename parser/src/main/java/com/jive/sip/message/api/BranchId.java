package com.jive.sip.message.api;

import com.jive.sip.base.api.Token;

import lombok.Value;

@Value
public class BranchId implements Comparable<BranchId> {

  private String value;

  public static final String MAGIC_COOKIE = "z9hG4bK";

  public boolean hasMagicCookie() {
    return this.value().startsWith(MAGIC_COOKIE);
  }

  @Override
  public int compareTo(final BranchId o) {
    return this.value.compareTo(o.value);
  }

  /**
   * Creates a BranchId with the magic cookie prepended.
   *
   * @param omnomnom
   *          The branch value (without the leading magic cookie).
   *
   * @return
   */

  public static BranchId withCookiePrepended(final CharSequence omnomnom) {
    return new BranchId(MAGIC_COOKIE + omnomnom.toString());
  }

  public static BranchId fromString(final CharSequence seq) {
    return new BranchId(seq.toString());
  }

  public static BranchId fromToken(final Token token) {
    return new BranchId(token.toString());
  }

  public String getValueWithoutCookie() {
    return (this.value().length() > MAGIC_COOKIE.length()) ? this.value().substring(MAGIC_COOKIE.length())
                                                           : this.value();
  }

  public Token asToken() {
    return Token.from(this.value);
  }

}
