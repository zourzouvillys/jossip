package io.rtcore.sip.message.parameters.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
  public boolean contains(final Token name) {
    for (final RawParameter p : this.raw) {
      if (p.name().equals(name)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Iterator<RawParameter> iterator() {
    return this.raw.iterator();
  }

  @Override
  public Stream<RawParameter> stream() {
    return this.raw.stream();
  }

  @Override
  public boolean contains(final String name) {
    return this.contains(Token.from(name));
  }

  @Override
  public Collection<RawParameter> getRawParameters() {
    return Collections.unmodifiableCollection(this.raw);
  }

  @Override
  public <T> Optional<T> getParameter(final SipParameterDefinition<T> parameterDefinition) {
    return parameterDefinition.parse(this);
  }

  @Override
  public Optional<String> getParameter(final String name) {
    final Token tok = Token.from(name);
    for (final RawParameter param : this.raw) {
      if (param.name().equals(tok)) {
        final ParameterValueVisitor<String> extractor = StringParameterExtractor.getInstance();
        final String str = param.value().apply(extractor);
        return Optional.<String>ofNullable(str);
      }
    }
    return Optional.empty();
  }

  @Override
  public Parameters withParameter(final Token name) {
    return this.withParameter(name, new FlagParameterValue());
  }

  @Override
  public Parameters withParameter(final String token) {
    return this.withParameter(Token.from(token));
  }

  @Override
  public Parameters withToken(final String token, final String value) {
    return this.withParameter(Token.from(token), Token.from(value));
  }

  @Override
  public Parameters withParameter(final Token name, final Token value) {
    return this.withParameter(name, new TokenParameterValue(value));
  }

  @Override
  public Parameters withParameter(final Token name, final QuotedString value) {
    return this.withParameter(name, new QuotedStringParameterValue(value.value()));
  }

  @Override
  public Parameters withParameter(final Token name, final HostAndPort value) {
    return this.withParameter(name, new HostAndPortParameterValue(value));
  }

  private <T> Parameters withParameter(final Token name, final ParameterValue<T> value) {
    final List<RawParameter> params = Lists.newArrayList(this.raw);
    params.add(new RawParameter(name, value));
    return this.withRaw(params);
  }

  @Override
  public Parameters withoutParameter(final Token name) {
    Preconditions.checkNotNull(name);
    final List<RawParameter> params = Lists.newArrayList();
    for (final RawParameter p : this.raw) {
      if (!p.name().equals(name)) {
        params.add(p);
      }
    }
    return this.withRaw(params);
  }

  @Override
  public Parameters withParameters(final Collection<RawParameter> parameters) {
    return this.withRaw(parameters);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for (final RawParameter param : this.raw) {
      sb.append(';').append(param.toString());
    }
    return sb.toString();
  }

  @Override
  public DefaultParameters withParameter(final RawParameter param) {
    final List<RawParameter> params = Lists.newArrayList(this.raw);
    params.add(param);
    return this.withRaw(params);
  }

  @Override
  public Parameters mergeParameters(final Parameters params) {
    if (params == null) {
      return this;
    }
    Parameters self = this;
    for (final RawParameter param : params.getRawParameters()) {
      self = self.withoutParameter(param.name()).withParameter(param);
    }
    return self;
  }

  @Override
  public boolean compareCommonParameters(final Parameters params) {
    for (final RawParameter rp : this.raw) {
      final Optional<String> other = params.getParameter(rp.name().toString());
      if (other.isPresent() && !other.get().equals(rp.value().apply(StringParameterExtractor.getInstance()))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public <T> Parameters withParameter(final SipParameterDefinition<T> def, final String value) {
    return this.withParameter(def.name(), def.toParameterValue(value));
  }

  @Override
  public <T> Parameters withParameter(final SipParameterDefinition<T> def, final Token value) {
    return this.withParameter(def, value.toString());
  }

  @Override
  public <T> Parameters withParameter(final SipParameterDefinition<T> def, final long value) {
    return this.withParameter(def.name(), def.toParameterValue(value));
  }

  @Override
  public <T> Parameters withParameter(final SipParameterDefinition<T> def, final HostAndPort value) {
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
    if (o == this) {
      return true;
    }
    if (!(o instanceof final DefaultParameters other)) {
      return false;
    }
    final Object this$raw = this.raw();
    final Object other$raw = other.raw();
    if (this$raw == null ? other$raw != null
                         : !this$raw.equals(other$raw)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    final int result = 1;
    final Object $raw = this.raw();
    return (result * PRIME)
      + ($raw == null ? 43
                      : $raw.hashCode());
  }

  private DefaultParameters withRaw(final Collection<RawParameter> raw) {
    return this.raw == raw ? this
                           : new DefaultParameters(raw);
  }

  public static DefaultParameters of(final Token name, final Token value) {
    return DefaultParameters.of(RawParameter.of(name, value));
  }

  private static DefaultParameters of(final RawParameter... vals) {
    return new DefaultParameters(Arrays.asList(vals));
  }

  public static DefaultParameters emptyParameters() {
    return EMPTY;
  }

  public static DefaultParameters of() {
    return EMPTY;
  }

  @Override
  public boolean isEmpty() {
    return this.raw.isEmpty();
  }

  @Override
  public Parameters filter(final Predicate<RawParameter> predicate) {
    return DefaultParameters.from(this.raw.stream().filter(predicate).collect(Collectors.toUnmodifiableList()));
  }

}
