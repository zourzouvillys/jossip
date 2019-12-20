package io.rtcore.sip.message.parameters.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.parameters.api.FlagParameterValue;
import io.rtcore.sip.message.parameters.api.HostAndPortParameterValue;
import io.rtcore.sip.message.parameters.api.ParameterValue;
import io.rtcore.sip.message.parameters.api.ParameterValueVisitor;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.QuotedString;
import io.rtcore.sip.message.parameters.api.QuotedStringParameterValue;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.SipParameterDefinition;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;

public final class DefaultParameters implements Parameters {
  private final Collection<RawParameter> raw;
  public static final DefaultParameters EMPTY = new DefaultParameters(Lists.<RawParameter>newArrayList());

  @Override
  public boolean contains(Token name) {
    for (RawParameter p : raw) {
      if (p.name().equals(name)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean contains(String name) {
    return this.contains(Token.from(name));
  }

  @Override
  public Collection<RawParameter> getRawParameters() {
    return Collections.unmodifiableCollection(raw);
  }

  @Override
  public <T> Optional<T> getParameter(SipParameterDefinition<T> parameterDefinition) {
    return parameterDefinition.parse(this);
  }

  @Override
  public Optional<String> getParameter(String name) {
    Token tok = Token.from(name);
    for (RawParameter param : this.raw) {
      if (param.name().equals(tok)) {
        ParameterValueVisitor<String> extractor = StringParameterExtractor.getInstance();
        String str = param.value().apply(extractor);
        return Optional.<String>ofNullable(str);
      }
    }
    return Optional.empty();
  }

  @Override
  public Parameters withParameter(Token name) {
    return this.withParameter(name, new FlagParameterValue());
  }

  @Override
  public Parameters withParameter(Token name, Token value) {
    return this.withParameter(name, new TokenParameterValue(value));
  }

  @Override
  public Parameters withParameter(Token name, QuotedString value) {
    return this.withParameter(name, new QuotedStringParameterValue(value.value()));
  }

  @Override
  public Parameters withParameter(Token name, HostAndPort value) {
    return this.withParameter(name, new HostAndPortParameterValue(value));
  }

  private <T> Parameters withParameter(Token name, ParameterValue<T> value) {
    List<RawParameter> params = Lists.newArrayList(raw);
    params.add(new RawParameter(name, value));
    return this.withRaw(params);
  }

  @Override
  public Parameters withoutParameter(Token name) {
    Preconditions.checkNotNull(name);
    List<RawParameter> params = Lists.newArrayList();
    Iterator<RawParameter> it = raw.iterator();
    while (it.hasNext()) {
      RawParameter p = it.next();
      if (!p.name().equals(name)) {
        params.add(p);
      }
    }
    return this.withRaw(params);
  }

  @Override
  public Parameters withParameters(Collection<RawParameter> parameters) {
    return this.withRaw(parameters);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (RawParameter param : this.raw) {
      sb.append(';').append(param.toString());
    }
    return sb.toString();
  }

  @Override
  public DefaultParameters withParameter(RawParameter param) {
    List<RawParameter> params = Lists.newArrayList(raw);
    params.add(param);
    return this.withRaw(params);
  }

  @Override
  public Parameters mergeParameters(Parameters params) {
    if (params == null) {
      return this;
    }
    Parameters self = this;
    for (RawParameter param : params.getRawParameters()) {
      self = self.withoutParameter(param.name()).withParameter(param);
    }
    return self;
  }

  @Override
  public boolean compareCommonParameters(Parameters params) {
    for (RawParameter rp : raw) {
      Optional<String> other = params.getParameter(rp.name().toString());
      if (other.isPresent() && !other.get().equals(rp.value().apply(StringParameterExtractor.getInstance()))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public <T> Parameters withParameter(SipParameterDefinition<T> def, String value) {
    return this.withParameter(def.name(), def.toParameterValue(value));
  }

  @Override
  public <T> Parameters withParameter(SipParameterDefinition<T> def, Token value) {
    return this.withParameter(def, value.toString());
  }

  @Override
  public <T> Parameters withParameter(SipParameterDefinition<T> def, long value) {
    return this.withParameter(def.name(), def.toParameterValue(value));
  }

  @Override
  public <T> Parameters withParameter(SipParameterDefinition<T> def, HostAndPort value) {
    return this.withParameter(def.name(), def.toParameterValue(value));
  }

  private DefaultParameters(final Collection<RawParameter> raw) {
    this.raw = raw;
  }

  public static DefaultParameters from(final Collection<RawParameter> raw) {
    return new DefaultParameters(raw);
  }

  public Collection<RawParameter> raw() {
    return this.raw;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof DefaultParameters)) return false;
    final DefaultParameters other = (DefaultParameters) o;
    final Object this$raw = this.raw();
    final Object other$raw = other.raw();
    if (this$raw == null ? other$raw != null : !this$raw.equals(other$raw)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $raw = this.raw();
    result = result * PRIME + ($raw == null ? 43 : $raw.hashCode());
    return result;
  }

  private DefaultParameters withRaw(final Collection<RawParameter> raw) {
    return this.raw == raw ? this : new DefaultParameters(raw);
  }
}
