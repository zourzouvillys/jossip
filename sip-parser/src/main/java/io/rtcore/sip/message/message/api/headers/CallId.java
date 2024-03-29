/**
 *
 */
package io.rtcore.sip.message.message.api.headers;

import java.io.Serializable;

/**
 *
 */

public final class CallId implements Serializable {

  private static final long serialVersionUID = 1L;
  private final String value;

  public CallId(final CharSequence value) {
    this.value = value.toString();
  }

  /**
   * returns the value of this callid, e.g [user@]host
   *
   * @return
   */
  public String getValue() {
    return this.value;
  }

  public String value() {
    return this.value;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof CallId other)) {
      return false;
    }
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
    int result = 1;
    final Object $value = this.value();
    return (result * PRIME)
        + ($value == null ? 43
                          : $value.hashCode());
  }

  @Override
  public String toString() {
    return "CallId(value=" + this.value() + ")";
  }

  public CallId withValue(final String value) {
    return this.value == value ? this
                               : new CallId(value);
  }

}
