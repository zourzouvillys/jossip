package io.rtcore.gateway;

import java.util.List;
import java.util.Set;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.zalando.problem.jackson.ProblemModule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import io.rtcore.gateway.engine.SipEngine;
import io.rtcore.gateway.rest.FlowResource;
import io.rtcore.gateway.rest.NICTResource;
import io.rtcore.gateway.rest.ServerTxnResource;

@Module
final class RestModule {

  private final int port;

  public RestModule(final int port) {
    this.port = port;
  }

  @Provides
  static ServiceManager serviceManager(final Set<Service> services) {
    return new ServiceManager(List.copyOf(services));
  }

  @Provides
  static ObjectMapper defaultMapper() {
    return new ObjectMapper()
      .registerModule(new ProblemModule())
      .registerModule(new JavaTimeModule())
      .registerModule(new GuavaModule())
      .registerModule(new Jdk8Module());
  }

  @Provides
  static ResourceConfig resourceConfig(final ObjectMapper defaultMapper, final SipEngine engine) {

    final ResourceConfig cfg = new ResourceConfig();

    // cfg.property(ServerProperties.OUTBOUND_CONTENT_LENGTH_BUFFER, 0);

    cfg.register(new AbstractBinder() {
      @Override
      protected void configure() {
        this.bind(engine).to(SipEngine.class);
      }
    });

    cfg.register(FlowResource.class);
    cfg.register(NICTResource.class);
    cfg.register(ServerTxnResource.class);

    cfg.register(new JacksonJsonProvider(defaultMapper));
    cfg.register(SseFeature.class);

    cfg.register(ProblemExceptionMapper.class);

    return cfg;

  }

  @Provides
  RestServer restServer(final ResourceConfig app) {
    return new RestServer(app, this.port);
  }

  @Provides
  @IntoSet
  static Service restService(final RestServer restServer) {
    return restServer;
  }

}
