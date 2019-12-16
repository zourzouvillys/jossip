package com.jive.sip.parameters.tools;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.impl.DefaultParameters;

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
