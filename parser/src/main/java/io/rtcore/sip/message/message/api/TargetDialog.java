package io.rtcore.sip.message.message.api;

import java.util.Optional;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.message.api.headers.CallId;
import io.rtcore.sip.message.parameters.api.BaseParameterizedObject;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;

public class TargetDialog extends BaseParameterizedObject<TargetDialog> {
  private CallId callId;

  public TargetDialog(CallId callId, Parameters parameters) {
    this.callId = callId;
    this.parameters = parameters;
  }

  public TargetDialog(DialogId dialogId) {
    this.callId = dialogId.callId();
    this.parameters = DefaultParameters.from(Lists.newArrayList(new RawParameter("local-tag", new TokenParameterValue(Token.from(dialogId.localTag()))), new RawParameter("remote-tag", new TokenParameterValue(Token.from(dialogId.remoteTag())))));
  }

  /**
   * returns the dialog id, or empty if there isn't both a local and remote tag.
   */
  public Optional<DialogId> asDialogId() {
    if (getLocalTag().isPresent() && getRemoteTag().isPresent()) {
      return Optional.of(new DialogId(callId(), getLocalTag().get(), getRemoteTag().get()));
    }
    return Optional.empty();
  }

  public Optional<String> getLocalTag() {
    return Optional.ofNullable(parameters.getParameter("local-tag").orElse(null));
  }

  public Optional<String> getRemoteTag() {
    return Optional.ofNullable(parameters.getParameter("remote-tag").orElse(null));
  }

  @Override
  public TargetDialog withParameters(Parameters parameters) {
    return new TargetDialog(callId, parameters);
  }

  public CallId callId() {
    return this.callId;
  }
}
