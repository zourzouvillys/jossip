/**
 * 
 */
package com.jive.sip.auth.headers;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.jive.sip.parameters.api.BaseParameterizedObject;
import com.jive.sip.parameters.api.FlagParameterValue;
import com.jive.sip.parameters.api.HostAndPortParameterValue;
import com.jive.sip.parameters.api.ParameterValueVisitor;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.QuotedStringParameterValue;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.TokenParameterValue;
import com.jive.sip.parameters.impl.DefaultParameters;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * An authorization header is a simple set of key values along with a single token that represents
 * the auth scheme (e.g, Basic, Digest, etc).
 */

@EqualsAndHashCode(callSuper = true)
public class Authorization extends BaseParameterizedObject<Authorization> {

  /**
   * The scheme.
   */

  @Getter
  private final String scheme;

  public Authorization(final String scheme) {
    this(scheme, DefaultParameters.EMPTY);
  }

  public Authorization(final String scheme, final Parameters parameters) {
    this.scheme = scheme;
    this.parameters = parameters;
  }

  public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append(scheme).append(' ');

    int count = 0;

    for (RawParameter p : this.parameters.getRawParameters()) {

      if (count++ > 0) {
        sb.append(",");
      }

      sb.append(p.getName());

      p.getValue().apply(new ParameterValueVisitor<Void>() {

        @Override
        public Void visit(FlagParameterValue parameter) {
          return null;
        }

        @Override
        public Void visit(TokenParameterValue parameter) {
          sb.append('=').append(parameter.getValue().toString());
          return null;
        }

        @Override
        public Void visit(QuotedStringParameterValue parameter) {
          sb.append('=').append(quote(parameter.getValue().toString()));
          return null;
        }

        @Override
        public Void visit(HostAndPortParameterValue parameter) {
          sb.append('=').append(parameter.getValue().toString());
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

}
