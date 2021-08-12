package io.rtcore.sip.message.uri;

import java.util.Optional;
import java.util.function.Function;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.parameters.api.BaseParameterizedObject;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parameters.impl.TokenParameterDefinition;

public final class TelUri extends BaseParameterizedObject<TelUri> implements Uri {
  private final String number;
  private static final TokenParameterDefinition P_TGRP = new TokenParameterDefinition(Token.from("tgrp"));

  public TelUri(final String number) {
    this(number, DefaultParameters.EMPTY);
  }

  public TelUri(final String number, final Parameters parameters) {
    this.number = number;
    this.parameters = parameters;
  }

  @Override
  public String getScheme() {
    return "tel";
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getScheme()).append(':');
    sb.append(this.number);
    if (this.parameters != null) {
      sb.append(this.parameters);
    }
    return sb.toString();
  }

  @Override
  public <T> T apply(UriVisitor<T> visitor) {
    if (visitor instanceof TelUriVisitor) {
      return ((TelUriVisitor<T>) visitor).visit(this);
    }
    return visitor.visit(this);
  }

  @Override
  public TelUri withParameters(Parameters parameters) {
    return new TelUri(this.number, parameters);
  }

  public TelUri withTrunkGroup(String value) {
    Parameters parameters = this.parameters.withParameter(Token.from("tgrp"), Token.from(value));
    return new TelUri(this.number, parameters);
  }

  public Optional<String> getTrunkGroup() {
    if (this.parameters != null) {
      return parameters.getParameter(P_TGRP).map(new Function<Token, String>() {
        @Override
        public String apply(Token input) {
          return input.toString();
        }
      });
    }
    return null;
  }

  public String number() {
    return this.number;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof TelUri)) return false;
    final TelUri other = (TelUri) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    final Object this$number = this.number();
    final Object other$number = other.number();
    if (this$number == null ? other$number != null : !this$number.equals(other$number)) return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof TelUri;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = super.hashCode();
    final Object $number = this.number();
    result = result * PRIME + ($number == null ? 43 : $number.hashCode());
    return result;
  }
}
