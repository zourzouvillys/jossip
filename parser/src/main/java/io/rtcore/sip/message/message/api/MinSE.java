package io.rtcore.sip.message.message.api;

import java.time.Duration;

import io.rtcore.sip.message.parameters.api.BaseParameterizedObject;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;

public class MinSE extends BaseParameterizedObject<MinSE> {
  private Duration duration;

  public MinSE(Duration duration) {
    this(duration, DefaultParameters.EMPTY);
  }

  public MinSE(Duration duration, Parameters params) {
    this.duration = duration;
    this.parameters = params;
  }

  @Override
  public MinSE withParameters(Parameters parameters) {
    return new MinSE(duration, parameters);
  }

  public Duration duration() {
    return this.duration;
  }
}
