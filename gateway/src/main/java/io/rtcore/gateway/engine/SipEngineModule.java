package io.rtcore.gateway.engine;

import dagger.Module;
import dagger.Provides;
import io.rtcore.sip.channels.connection.SipRoute;

@Module
public class SipEngineModule {

  private final SipRoute route;

  public SipEngineModule(final SipRoute route) {
    this.route = route;
  }

  @Provides
  SipEngine sipEngine() {
    return new SipEngine(this.route);
  }

}
