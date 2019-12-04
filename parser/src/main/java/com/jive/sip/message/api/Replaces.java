package com.jive.sip.message.api;

import java.util.List;

import com.google.common.collect.Lists;
import com.jive.sip.message.api.headers.CallId;
import com.jive.sip.parameters.api.BaseParameterizedObject;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.TokenParameterValue;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parameters.impl.FlagParameterDefinition;
import com.jive.sip.parameters.impl.TokenParameterDefinition;

import lombok.Getter;

public class Replaces extends BaseParameterizedObject<Replaces>
{

  public static TokenParameterDefinition FromTag = new TokenParameterDefinition("from-tag");
  public static TokenParameterDefinition ToTag = new TokenParameterDefinition("to-tag");
  public static FlagParameterDefinition EarlyOnly = new FlagParameterDefinition("early-only");


  @Getter
  private final CallId callId;

  public Replaces(final CallId callId, final Parameters params)
  {
    this.callId = callId;
    this.parameters = params;
  }

  @Override
  public Replaces withParameters(final Parameters parameters)
  {
    return new Replaces(this.callId, parameters);
  }

  @Override
  public String toString()
  {
    return new StringBuilder().append(this.callId.getValue()).append(this.parameters).toString();
  }

  public String getToTag()
  {
    return this.parameters.getParameter(ToTag).get().toString();
  }

  public String getFromTag()
  {
    return this.parameters.getParameter(FromTag).get().toString();
  }

  public boolean isEarlyOnly()
  {
    return this.parameters.getParameter(EarlyOnly).isPresent();
  }

  public static Replaces fromLocal(final DialogId dialogId, final boolean earlyOnly)
  {
    return create(dialogId.getCallId(), dialogId.getLocalTag(), dialogId.getRemoteTag(), earlyOnly);
  }


  public static Replaces fromRemote(final DialogId dialogId, final boolean earlyOnly)
  {
    return create(dialogId.getCallId(), dialogId.getRemoteTag(), dialogId.getLocalTag(), earlyOnly);
  }


  public static Replaces create(final CallId callId, final String to, final String from, final boolean earlyOnly)
  {
    final List<RawParameter> params = Lists.newLinkedList();
    params.add(new RawParameter(ToTag.getName(), new TokenParameterValue(to)));
    params.add(new RawParameter(FromTag.getName(), new TokenParameterValue(from)));
    if (earlyOnly)
    {
      params.add(new RawParameter("early-only"));
    }
    return new Replaces(callId, DefaultParameters.from(params));
  }

}
