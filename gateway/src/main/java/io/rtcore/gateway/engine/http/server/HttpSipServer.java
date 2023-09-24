package io.rtcore.gateway.engine.http.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;

import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.google.common.util.concurrent.AbstractService;

import io.netty.channel.nio.NioEventLoopGroup;
import reactor.netty.DisposableServer;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

/**
 * HTTP server for controlling SIP network transactions asynchronously.
 *
 * This HTTP server exposes a comprehensive API for managing SIP network operations. It enables
 * clients to send SIP requests, receive their responses or errors, manage connections (including
 * raw frame transmission), deliver in-dialog ACKs, and provide SIP responses for active
 * transactions.
 *
 * transactions are always initiated within the scope of a channel which may be a specific channel
 * ID (for sending over an existing flow), or for datagrams, a local and remote socket address.
 *
 * for TLS, outgoing connections require a little more configuration - as we need to validate the
 * TLS context itself. this is done by configuring TLS contexts and then referencing the context for
 * the channel.
 *
 */

public class HttpSipServer extends AbstractService {

  private DisposableServer server;
  private InetSocketAddress address;
  private HostAndPort endpoint;
  private final ExternalSipHandlerAdapter adapter;
  private final Supplier<? extends SocketAddress> bindAddressSupplier;

  /**
   *
   */

  public HttpSipServer(final ExternalSipServerHandler handler) {
    this(handler, 0);
  }

  public HttpSipServer(final ExternalSipServerHandler handler, final int port) {
    this.adapter = new ExternalSipHandlerAdapter(handler);
    this.bindAddressSupplier = () -> new InetSocketAddress(port);
  }

  @Override
  protected void doStart() {
    this.server =
      HttpServer.create()
        // restrict to a single thread.
        .runOn(new NioEventLoopGroup(1))
        .handle(this::handleServerRequest)
        .protocol(HttpProtocol.H2C)
        // .http2Settings(v -> v.maxConcurrentStreams(50_000).initialWindowSize(8192))
        .accessLog(true)
        .bindAddress(this.bindAddressSupplier)
        .bindNow();
    this.address = (InetSocketAddress) this.server.address();
    this.endpoint = HostAndPort.fromParts(InetAddresses.toUriString(this.address.getAddress()), this.address.getPort());
    this.notifyStarted();
  }

  @Override
  protected void doStop() {
    this.server.disposeNow();
    this.server.onDispose().block();
    this.notifyStopped();
  }

  /**
   * internal handler for the incoming requests.
   *
   * @return a publisher that will complete once the request has terminated.
   *
   */

  private Publisher<Void> handleServerRequest(final HttpServerRequest req, final HttpServerResponse res) {
    return this.adapter.adapt().apply(req, res);
  }

  public String url(final String path) {
    return String.format("http://%s/%s", this.endpoint.toString(), path);
  }

}
