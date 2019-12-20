/**
 * 
 */
package com.jive.sip.message.api.headers;

import java.util.Optional;

import com.jive.sip.parameters.api.BaseParameterizedObject;
import com.jive.sip.parameters.api.Parameters;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 */
public final class RetryAfter extends BaseParameterizedObject<RetryAfter> {
  private final int delta;
  private final String comment;

  public RetryAfter(int delta) {
    this(delta, null, null);
  }

  public RetryAfter(int delta, String comment) {
    this(delta, comment, null);
  }

  public RetryAfter(int delta, String comment, Parameters parameters) {
    this.delta = delta;
    this.comment = comment;
    this.parameters = parameters;
  }

  public Optional<String> getComment() {
    return Optional.ofNullable(comment);
  }

  @Override
  public RetryAfter withParameters(Parameters parameters) {
    return new RetryAfter(this.delta, this.comment, parameters);
  }

  public int delta() {
    return this.delta;
  }

  public String comment() {
    return this.comment;
  }

  @Override
  public String toString() {
    return "RetryAfter(delta=" + this.delta() + ", comment=" + this.comment() + ")";
  }

  public RetryAfter withDelta(final int delta) {
    return this.delta == delta ? this : new RetryAfter(delta, this.comment);
  }

  public RetryAfter withComment(final String comment) {
    return this.comment == comment ? this : new RetryAfter(this.delta, comment);
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof RetryAfter)) return false;
    final RetryAfter other = (RetryAfter) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    if (this.delta() != other.delta()) return false;
    final Object this$comment = this.comment();
    final Object other$comment = other.comment();
    if (this$comment == null ? other$comment != null : !this$comment.equals(other$comment)) return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof RetryAfter;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = super.hashCode();
    result = result * PRIME + this.delta();
    final Object $comment = this.comment();
    result = result * PRIME + ($comment == null ? 43 : $comment.hashCode());
    return result;
  }
}
