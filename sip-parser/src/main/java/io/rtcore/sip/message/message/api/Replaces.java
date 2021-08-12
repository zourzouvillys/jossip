package io.rtcore.sip.message.message.api;

import java.util.List;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.message.api.headers.CallId;
import io.rtcore.sip.message.parameters.api.BaseParameterizedObject;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parameters.impl.FlagParameterDefinition;
import io.rtcore.sip.message.parameters.impl.TokenParameterDefinition;

public class Replaces extends BaseParameterizedObject<Replaces> {
  public static TokenParameterDefinition FromTag = new TokenParameterDefinition("from-tag");
  public static TokenParameterDefinition ToTag = new TokenParameterDefinition("to-tag");
  public static FlagParameterDefinition EarlyOnly = new FlagParameterDefinition("early-only");
  private final CallId callId;

  public Replaces(final CallId callId, final Parameters params) {
    this.callId = callId;
    this.parameters = params;
  }

  @Override
  public Replaces withParameters(final Parameters parameters) {
    return new Replaces(this.callId, parameters);
  }

  @Override
  public String toString() {
    return new StringBuilder().append(this.callId.getValue()).append(this.parameters).toString();
  }

  public String getToTag() {
    return this.parameters.getParameter(ToTag).get().toString();
  }

  public String getFromTag() {
    return this.parameters.getParameter(FromTag).get().toString();
  }

  public boolean isEarlyOnly() {
    return this.parameters.getParameter(EarlyOnly).isPresent();
  }

  public static Replaces fromLocal(final DialogId dialogId, final boolean earlyOnly) {
    return create(dialogId.callId(), dialogId.localTag(), dialogId.remoteTag(), earlyOnly);
  }

  public static Replaces fromRemote(final DialogId dialogId, final boolean earlyOnly) {
    return create(dialogId.callId(), dialogId.remoteTag(), dialogId.localTag(), earlyOnly);
  }

  public static Replaces create(final CallId callId, final String to, final String from, final boolean earlyOnly) {
    final List<RawParameter> params = Lists.newLinkedList();
    params.add(new RawParameter(ToTag.name(), new TokenParameterValue(to)));
    params.add(new RawParameter(FromTag.name(), new TokenParameterValue(from)));
    if (earlyOnly) {
      params.add(new RawParameter("early-only"));
    }
    return new Replaces(callId, DefaultParameters.from(params));
  }

  public CallId callId() {
    return this.callId;
  }
}
