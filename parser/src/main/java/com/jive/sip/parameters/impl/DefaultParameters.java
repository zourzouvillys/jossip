package com.jive.sip.parameters.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.jive.sip.base.api.Token;
import com.jive.sip.parameters.api.FlagParameterValue;
import com.jive.sip.parameters.api.HostAndPortParameterValue;
import com.jive.sip.parameters.api.ParameterValue;
import com.jive.sip.parameters.api.ParameterValueVisitor;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.QuotedString;
import com.jive.sip.parameters.api.QuotedStringParameterValue;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.SipParameterDefinition;
import com.jive.sip.parameters.api.TokenParameterValue;

import lombok.AccessLevel;
import lombok.Value;
import lombok.experimental.Wither;

@Value(staticConstructor = "from")
public class DefaultParameters implements Parameters {

  @Wither(AccessLevel.PRIVATE)
  private final Collection<RawParameter> raw;

  public static final DefaultParameters EMPTY =
    new DefaultParameters(
      Lists.<RawParameter>newArrayList());

  @Override
  public boolean contains(Token name) {
    for (RawParameter p : raw) {
      if (p.getName().equals(name)) {
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
      if (param.getName().equals(tok)) {
        ParameterValueVisitor<String> extractor = StringParameterExtractor.getInstance();
        String str = param.getValue().apply(extractor);
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
    return this.withParameter(name, new QuotedStringParameterValue(value.getValue()));
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
      if (!p.getName().equals(name)) {
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
      self = self.withoutParameter(param.getName()).withParameter(param);
    }

    return self;

  }

  @Override
  public boolean compareCommonParameters(Parameters params) {
    for (RawParameter rp : raw) {
      Optional<String> other = params.getParameter(rp.getName().toString());
      if (other.isPresent()
        && !other.get().equals(rp.getValue().apply(StringParameterExtractor.getInstance()))) {
        return false;
      }
    }

    return true;
  }

  @Override
  public <T> Parameters withParameter(SipParameterDefinition<T> def, String value) {
    return this.withParameter(def.getName(), def.toParameterValue(value));
  }

  @Override
  public <T> Parameters withParameter(SipParameterDefinition<T> def, Token value) {
    return this.withParameter(def, value.toString());
  }

  @Override
  public <T> Parameters withParameter(SipParameterDefinition<T> def, long value) {
    return this.withParameter(def.getName(), def.toParameterValue(value));
  }

  @Override
  public <T> Parameters withParameter(SipParameterDefinition<T> def, HostAndPort value) {
    return this.withParameter(def.getName(), def.toParameterValue(value));
  }

}
