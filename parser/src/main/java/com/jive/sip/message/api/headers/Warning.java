/**
 * 
 */
package com.jive.sip.message.api.headers;

/**
 * Warning Field
 */
public final class Warning {
  private final int code;
  private final CharSequence agent;
  private final CharSequence text;

  public Warning(final int code, final CharSequence agent, final CharSequence text) {
    this.code = code;
    this.agent = agent;
    this.text = text;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof Warning)) return false;
    final Warning other = (Warning) o;
    if (this.code() != other.code()) return false;
    final Object this$agent = this.agent();
    final Object other$agent = other.agent();
    if (this$agent == null ? other$agent != null : !this$agent.equals(other$agent)) return false;
    final Object this$text = this.text();
    final Object other$text = other.text();
    if (this$text == null ? other$text != null : !this$text.equals(other$text)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + this.code();
    final Object $agent = this.agent();
    result = result * PRIME + ($agent == null ? 43 : $agent.hashCode());
    final Object $text = this.text();
    result = result * PRIME + ($text == null ? 43 : $text.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "Warning(code=" + this.code() + ", agent=" + this.agent() + ", text=" + this.text() + ")";
  }

  public Warning withCode(final int code) {
    return this.code == code ? this : new Warning(code, this.agent, this.text);
  }

  public Warning withAgent(final CharSequence agent) {
    return this.agent == agent ? this : new Warning(this.code, agent, this.text);
  }

  public Warning withText(final CharSequence text) {
    return this.text == text ? this : new Warning(this.code, this.agent, text);
  }

  public int code() {
    return this.code;
  }

  public CharSequence agent() {
    return this.agent;
  }

  public CharSequence text() {
    return this.text;
  }
}
