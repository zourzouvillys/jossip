package io.rtcore.sip.message.parameters.tools;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;

public class ParameterBuilder {
  public static DefaultParameters from(final Collection<RawParameter> params) {
    return DefaultParameters.from(params);
  }

  private final List<RawParameter> params = Lists.newArrayList();

  public ParameterBuilder with(final String name) {
    this.params.add(new RawParameter(name));
    return this;
  }

  public Parameters build() {
    return DefaultParameters.from(this.params);
  }

}
