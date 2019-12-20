package com.jive.sip.message.api;

import java.util.Optional;

import com.jive.sip.base.api.Token;
import com.jive.sip.message.SipResponseStatus;
import com.jive.sip.parameters.api.BaseParameterizedObject;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.QuotedString;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parameters.impl.QuotedStringParameterDefinition;
import com.jive.sip.parameters.impl.TokenParameterDefinition;

public class Reason extends BaseParameterizedObject<Reason> {
  public static final TokenParameterDefinition Cause = new TokenParameterDefinition("cause");
  public static final QuotedStringParameterDefinition Text = new QuotedStringParameterDefinition("text");
  private final CharSequence protocol;

  public Reason(final CharSequence protocol, final Parameters parameters) {
    this.protocol = protocol;
    this.parameters = parameters;
  }

  public Optional<Integer> cause() {
    return getParameter(Cause).map(v -> Integer.parseInt(v.toString()));
  }

  public Optional<String> text() {
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
    return new Reason("SIP", DefaultParameters.EMPTY).withParameter(Cause.name(), Token.from(status.code())).withParameter(Text.name(), QuotedString.from(status.reason()));
  }

  public Optional<SipResponseStatus> asSipStatus() {
    if ("SIP".equals(protocol())) {
      return Optional.of(SipResponseStatus.fromCode(cause().get()).withReason(text().orElse(null)));
    }
    return Optional.empty();
  }

  public CharSequence protocol() {
    return this.protocol;
  }
}
