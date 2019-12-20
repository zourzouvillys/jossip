package io.rtcore.sip.message.message.api;

import java.util.Optional;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.parameters.api.BaseParameterizedObject;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;

public class SessionExpires extends BaseParameterizedObject<SessionExpires> {

  public enum Refresher {
    Server("uas"), Client("uac");
    private String protocolValue;

    private Refresher(String value) {
      this.protocolValue = value;
    }

    public static Refresher fromProtocolValue(String e) {
      switch (e.toLowerCase()) {
      case "uas": 
        return Server;
      case "uac": 
        return Client;
      }
      throw new RuntimeException(e);
    }

    public String protocolValue() {
      return this.protocolValue;
    }
  }

  private long duration;

  public SessionExpires(long duration) {
    this(duration, DefaultParameters.EMPTY);
  }

  public SessionExpires(long duration, Refresher refresher) {
    this(duration, DefaultParameters.from(Lists.newArrayList(new RawParameter("refresher", new TokenParameterValue(refresher.protocolValue())))));
  }

  public SessionExpires(long duration, Parameters params) {
    this.duration = duration;
    this.parameters = params;
  }

  @Override
  public SessionExpires withParameters(Parameters parameters) {
    return new SessionExpires(duration, parameters);
  }

  public Optional<Refresher> getRefresher() {
    return Optional.ofNullable(parameters.getParameter("refresher").map(e -> Refresher.fromProtocolValue(e)).orElse(null));
  }

  public long duration() {
    return this.duration;
  }
}
