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

import lombok.Getter;

public class ContentDisposition extends BaseParameterizedObject<ContentDisposition> {

  public static final Token Required = Token.from("required");

  public static final Token Optional = Token.from("optional");

  public static final ContentDisposition SessionRequired = new ContentDisposition("session", true);

  public static TokenParameterDefinition Handling = new TokenParameterDefinition("handling");

  @Getter
  private final String value;

  public ContentDisposition(final CharSequence value, final Parameters parameters) {
    this.value = value.toString();
    this.parameters = parameters;
  }

  public ContentDisposition(final String name) {
    this(name, DefaultParameters.EMPTY);
  }

  public ContentDisposition(final String name, final boolean required) {
    this(
      name,
      (required)
                 ? DefaultParameters.from(
                   Lists.newArrayList(new RawParameter(
                     "handling",
                     new TokenParameterValue(ContentDisposition.Required))))
                 : DefaultParameters.EMPTY);
  }

  public Optional<Token> getHandling() {
    return this.parameters.getParameter(Handling);
  }

  @Override
  public ContentDisposition withParameters(final Parameters parameters) {
    return new ContentDisposition(this.value, parameters);
  }

}
