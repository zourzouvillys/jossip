package io.rtcore.sip.message.message.api;

import java.util.Optional;

import io.rtcore.sip.message.base.api.Token;

public final class BranchId implements Comparable<BranchId> {
  private final String value;
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

  public Optional<String> getValueWithoutCookie() {
    // http://bugs.sipit.net/show_bug.cgi?id=661
    return this.value.toLowerCase().startsWith(MAGIC_COOKIE.toLowerCase()) ? Optional.of(this.value().substring(MAGIC_COOKIE.length()))
                                                                           : Optional.empty();
  }

  public Token asToken() {
    return Token.from(this.value);
  }

  public BranchId(final String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof BranchId)) {
      return false;
    }
    final BranchId other = (BranchId) o;
    final Object this$value = this.value();
    final Object other$value = other.value();
    if (this$value == null ? other$value != null
        : !this$value.equals(other$value)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    final int result = 1;
    final Object $value = this.value();
    return (result * PRIME)
        + ($value == null ? 43
                          : $value.hashCode());
  }

  @Override
  public String toString() {
    return "BranchId(value=" + this.value() + ")";
  }
}
