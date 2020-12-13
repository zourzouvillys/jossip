/**
 * 
 */
package io.rtcore.sip.message.auth.headers;

import java.util.Objects;
import java.util.Optional;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;

import io.rtcore.sip.message.parameters.api.BaseParameterizedObject;
import io.rtcore.sip.message.parameters.api.FlagParameterValue;
import io.rtcore.sip.message.parameters.api.HostAndPortParameterValue;
import io.rtcore.sip.message.parameters.api.ParameterValueVisitor;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.QuotedStringParameterValue;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;

/**
 * An authorization header is a simple set of key values along with a single token that represents
 * the auth scheme (e.g, Basic, Digest, etc).
 */

public class Authorization extends BaseParameterizedObject<Authorization> {
  /**
   * The scheme.
   */
  private final String scheme;

  public Authorization(final String scheme) {
    this(scheme, DefaultParameters.EMPTY);
  }

  public Authorization(final String scheme, final Parameters parameters) {
    this.scheme = scheme;
    this.parameters = parameters;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(scheme).append(' ');
    int count = 0;
    for (RawParameter p : this.parameters.getRawParameters()) {
      if (count++ > 0) {
        sb.append(",");
      }
      sb.append(p.name());
      p.value().apply(new ParameterValueVisitor<Void>() {
        @Override
        public Void visit(FlagParameterValue parameter) {
          return null;
        }

        @Override
        public Void visit(TokenParameterValue parameter) {
          sb.append('=').append(parameter.value().toString());
          return null;
        }

        @Override
        public Void visit(QuotedStringParameterValue parameter) {
          sb.append('=').append(quote(parameter.value().toString()));
          return null;
        }

        @Override
        public Void visit(HostAndPortParameterValue parameter) {
          sb.append('=').append(parameter.value().toString());
          return null;
        }
      });
    }
    return sb.toString();
  }

  @Override
  public Authorization withParameters(final Parameters parameters) {
    return new Authorization(scheme, parameters);
  }

  static final Escaper ESCAPER =
    Escapers.builder()
      .addEscape('\\', "\\\\")
      .addEscape('"', "\\\"")
      .build();

  private String quote(String str) {
    return '"' + ESCAPER.escape(str) + '"';
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof Authorization))
      return false;
    final Authorization other = (Authorization) o;
    if (!other.canEqual((Object) this))
      return false;
    if (!super.equals(o))
      return false;
    final Object this$scheme = this.scheme();
    final Object other$scheme = other.scheme();
    if (this$scheme == null ? other$scheme != null
                            : !this$scheme.equals(other$scheme))
      return false;
    return true;
  }

  @Override
  protected boolean canEqual(final Object other) {
    return other instanceof Authorization;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = super.hashCode();
    final Object $scheme = this.scheme();
    result =
      (result * PRIME)
        + ($scheme == null ? 43
                           : $scheme.hashCode());
    return result;
  }

  /**
   * The scheme.
   */
  public String scheme() {
    return this.scheme;
  }

  public <T> Optional<T> as(Class<T> klass) {
    return Optional.empty();
  }

}
