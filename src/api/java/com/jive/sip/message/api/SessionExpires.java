package com.jive.sip.message.api;

import java.util.Optional;

import com.google.common.collect.Lists;
import com.jive.sip.parameters.api.BaseParameterizedObject;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.TokenParameterValue;
import com.jive.sip.parameters.impl.DefaultParameters;

import lombok.Getter;

public class SessionExpires extends BaseParameterizedObject<SessionExpires>
{

  public enum Refresher
  {

    Server("uas"),
    Client("uac");

    @Getter
    private String protocolValue;

    private Refresher(String value)
    {
      this.protocolValue = value;
    }

    public static Refresher fromProtocolValue(String e)
    {
      switch (e.toLowerCase())
      {
        case "uas":
          return Server;
        case "uac":
          return Client;
      }
      throw new RuntimeException(e);
    }

  }

  @Getter
  private long duration;

  public SessionExpires(long duration)
  {
    this(duration, DefaultParameters.EMPTY);
  }

  public SessionExpires(long duration, Refresher refresher)
  {
    this(duration, DefaultParameters.from(Lists.newArrayList(new RawParameter("refresher",
        new TokenParameterValue(refresher.getProtocolValue())))));
  }

  public SessionExpires(long duration, Parameters params)
  {
    this.duration = duration;
    this.parameters = params;
  }

  @Override
  public SessionExpires withParameters(Parameters parameters)
  {
    return new SessionExpires(duration, parameters);
  }

  public Optional<Refresher> getRefresher()
  {
    return Optional.ofNullable(parameters.getParameter("refresher").map(e -> Refresher.fromProtocolValue(e)).orElse(null));
  }

}
