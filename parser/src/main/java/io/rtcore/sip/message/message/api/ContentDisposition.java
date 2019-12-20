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

public class ContentDisposition extends BaseParameterizedObject<ContentDisposition> {
  public static final Token Required = Token.from("required");
  public static final Token Optional = Token.from("optional");
  public static final ContentDisposition SessionRequired = new ContentDisposition("session", true);
  public static TokenParameterDefinition Handling = new TokenParameterDefinition("handling");
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
      (required) ? DefaultParameters.from(Lists.newArrayList(new RawParameter("handling", new TokenParameterValue(ContentDisposition.Required))))
                 : DefaultParameters.EMPTY);
  }

  public Optional<Token> getHandling() {
    return this.parameters.getParameter(Handling);
  }

  @Override
  public ContentDisposition withParameters(final Parameters parameters) {
    return new ContentDisposition(this.value, parameters);
  }

  public String value() {
    return this.value;
  }
}
