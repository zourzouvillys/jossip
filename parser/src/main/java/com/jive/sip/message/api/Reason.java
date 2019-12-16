package com.jive.sip.message.api;

import java.util.Optional;

import com.jive.sip.base.api.Token;
import com.jive.sip.parameters.api.BaseParameterizedObject;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.QuotedString;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parameters.impl.QuotedStringParameterDefinition;
import com.jive.sip.parameters.impl.TokenParameterDefinition;

import lombok.Getter;

public class Reason extends BaseParameterizedObject<Reason> {

  public static final TokenParameterDefinition Cause = new TokenParameterDefinition("cause");
  public static final QuotedStringParameterDefinition Text =
    new QuotedStringParameterDefinition(
      "text");

  @Getter
  private final CharSequence protocol;

  public Reason(final CharSequence protocol, final Parameters parameters) {
    this.protocol = protocol;
    this.parameters = parameters;
  }

  public Optional<Integer> getCause() {
    return getParameter(Cause).map(v -> Integer.parseInt(v.toString()));
  }

  public Optional<String> getText() {
    return getParameter(Text);
  }

  @Override
  public Reason withParameters(final Parameters parameters) {
    return new Reason(this.protocol, parameters);
  }

  /**
   * Helper to create a reason header from a {@link SipResponseStatus}
   *
   * @param status
   * @return
   */

  public static Reason fromSipStatus(final SipResponseStatus status) {
    return new Reason("SIP", DefaultParameters.EMPTY)
      .withParameter(Cause.getName(), Token.from(status.getCode()))
      .withParameter(Text.getName(), QuotedString.from(status.getReason()));
  }

  public Optional<SipResponseStatus> asSipStatus() {
    if ("SIP".equals(getProtocol())) {
      return Optional.of(SipResponseStatus.fromCode(getCause().get())
        .withReason(getText().orElse(null)));
    }
    return Optional.empty();
  }

}
