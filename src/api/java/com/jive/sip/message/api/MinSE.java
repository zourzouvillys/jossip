package com.jive.sip.message.api;

import java.time.Duration;

import com.jive.sip.parameters.api.BaseParameterizedObject;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.impl.DefaultParameters;

import lombok.Getter;

public class MinSE extends BaseParameterizedObject<MinSE>
{

  @Getter
  private Duration duration;

  public MinSE(Duration duration)
  {
    this(duration, DefaultParameters.EMPTY);
  }

  public MinSE(Duration duration, Parameters params)
  {
    this.duration = duration;
    this.parameters = params;
  }

  @Override
  public MinSE withParameters(Parameters parameters)
  {
    return new MinSE(duration, parameters);
  }

}
