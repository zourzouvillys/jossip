package io.rtcore.gateway;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http2.Http2AddOn;
import org.glassfish.grizzly.http2.Http2Configuration;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.google.common.util.concurrent.AbstractService;

final class RestServer extends AbstractService {

  private final HttpServer server;

  /**
   * we never block, a single server thread is sufficient.
   *
   * @param app
   * @param baseURI
   */

  public RestServer(final ResourceConfig app, final URI baseURI) {

    this.server = GrizzlyHttpServerFactory.createHttpServer(baseURI, app, false);

    final Http2Configuration configuration = Http2Configuration.builder().build();

    for (final NetworkListener l : this.server.getListeners()) {

      final Http2AddOn http2Addon = new Http2AddOn(configuration);

      l.registerAddOn(http2Addon);

      final TCPNIOTransport t = l.getTransport();

      if (t != null) {
        t.setSelectorRunnersCount(1);
        t.setWorkerThreadPoolConfig(
          ThreadPoolConfig.defaultConfig()
            .setCorePoolSize(1)
            .setMaxPoolSize(1));
      }

    }

  }

  public RestServer(final ResourceConfig app, final int port) {
    this(app, URI.create("http://0.0.0.0:" + port));
  }

  @Override
  protected void doStart() {
    try {
      this.server.start();
      this.notifyStarted();
    }
    catch (final IOException cause) {
      this.notifyFailed(cause);
    }
  }

  @Override
  protected void doStop() {
    this.server.shutdownNow();
    this.notifyStopped();
  }

}
