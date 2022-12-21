package io.rtcore.gateway.engine;

import java.util.ServiceLoader;

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
  SipEngine sipEngine(final BackendProvider backendProvider) {
    return new SipEngine(backendProvider, this.route);
  }

  @Provides
  BackendProvider backendProvider() {
    return ServiceLoader.load(BackendProvider.class)
      .findFirst()
      .orElseThrow();
  }

}
