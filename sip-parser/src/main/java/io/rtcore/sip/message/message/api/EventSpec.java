package io.rtcore.sip.message.message.api;

import java.util.Optional;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.parameters.api.BaseParameterizedObject;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parameters.impl.TokenParameterDefinition;

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
