/**
 * 
 */
package io.rtcore.sip.message.message.api.headers;

/**
 */
public final class RValue {
  private final CharSequence namespace;
  private final CharSequence priority;

  public RValue(CharSequence namespace, CharSequence priority) {
    this.namespace = namespace;
    this.priority = priority;
  }

  public CharSequence namespace() {
    return this.namespace;
  }

  public CharSequence priority() {
    return this.priority;
  }

  @Override
  public String toString() {
    return "RValue(namespace=" + this.namespace() + ", priority=" + this.priority() + ")";
  }

  public RValue withNamespace(final CharSequence namespace) {
    return this.namespace == namespace ? this : new RValue(namespace, this.priority);
  }

  public RValue withPriority(final CharSequence priority) {
    return this.priority == priority ? this : new RValue(this.namespace, priority);
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof RValue)) return false;
    final RValue other = (RValue) o;
    final Object this$namespace = this.namespace();
    final Object other$namespace = other.namespace();
    if (this$namespace == null ? other$namespace != null : !this$namespace.equals(other$namespace)) return false;
    final Object this$priority = this.priority();
    final Object other$priority = other.priority();
    if (this$priority == null ? other$priority != null : !this$priority.equals(other$priority)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $namespace = this.namespace();
    result = result * PRIME + ($namespace == null ? 43 : $namespace.hashCode());
    final Object $priority = this.priority();
    result = result * PRIME + ($priority == null ? 43 : $priority.hashCode());
    return result;
  }
}
