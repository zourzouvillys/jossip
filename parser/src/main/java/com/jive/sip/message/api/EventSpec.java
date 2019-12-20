package com.jive.sip.message.api;

import java.util.Optional;

import com.google.common.collect.Lists;
import com.jive.sip.base.api.Token;
import com.jive.sip.parameters.api.BaseParameterizedObject;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.TokenParameterValue;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parameters.impl.TokenParameterDefinition;

public class EventSpec extends BaseParameterizedObject<EventSpec> {
  public static TokenParameterDefinition Id = new TokenParameterDefinition("id");
  private final String name;

  public EventSpec(final CharSequence name, final Parameters parameters) {
    this.name = name.toString();
    this.parameters = parameters;
  }

  public EventSpec(final String name) {
    this(name, DefaultParameters.EMPTY);
  }

  public EventSpec(final String name, final String id) {
    this(name, (id != null) ? DefaultParameters.from(Lists.newArrayList(new RawParameter("id", new TokenParameterValue(id)))) : DefaultParameters.EMPTY);
  }

  public Optional<Token> getId() {
    return this.parameters.getParameter(Id);
  }

  @Override
  public EventSpec withParameters(final Parameters parameters) {
    return new EventSpec(this.name, parameters);
  }

  public String name() {
    return this.name;
  }
}
