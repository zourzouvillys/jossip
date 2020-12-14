package io.rtcore.sip.proxy.transport.stream.client;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subjects.SingleSubject;
import io.rtcore.sip.proxy.MessageWriter;
import io.rtcore.sip.proxy.actions.OpenStream;
import io.rtcore.sip.proxy.chronicle.ChronicleMessageWriter;
import io.rtcore.sip.proxy.http.HttpServer;
import picocli.CommandLine.Command;

/**
 * service which listens for connection commands, and then attempts to establish a stream connection
 * to the requested target. once connected, the stream is handled identically to an incoming
 * connection; the only difference being the policy and routing rules are provided at connection
 * request time rather than on the connection being established.
 * 
 * a pending connection can send a limited number of requests, even though the connection does not
 * actually exist. this is important for latency, as we don't want a full notification round trip to
 * send a request if we are waiting on a connection being established. however, this has some
 * limitations. this should not be used if multiple connections are attempted in parallel and any
 * one of them could win.
 * 
 * a pending transmission has a different lifecycle than a normal send, as a normal send will only
 * ever be accepted if there is sufficient buffer space for transmission of it, otherwise it will be
 * rejected. an early send message on an unconnected socket will be accepted but will submit an
 * event when it is either sent *or* it fails due to the connection failing.
 * 
 * @author theo
 *
 */

@Command(name = "stream:connect")
public class StreamConnectCommand implements Runnable {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StreamConnectCommand.class);

  //
  private final EventBus eventBus = new EventBus();
  private final ClientConnectorFactory clientHandlerFactory = new ClientConnectorFactory();;

  private NioEventLoopGroup elg;
  private ClientStreamRegistry registry;
  private MessageWriter writer;

  public StreamConnectCommand() {
  }

  @Subscribe
  public void handleOpenConnection(OpenStream open) {

    HostAndPort remote = HostAndPort.fromString(open.remote());
    InetAddress addr = InetAddresses.forString(remote.getHost());
    int port = remote.getPortOrDefault(5060);
    InetSocketAddress remoteAddress = new InetSocketAddress(addr, port);

    //
    // SslContext sslctx =
    // SslContextBuilder.forClient()
    // .build();

    ChannelHandler channelHandler = clientHandlerFactory.createChannelHandler(open).apply(eventBus);

    Single<Channel> subject = connect(remoteAddress, channelHandler);

    subject.subscribe(
      ch -> {
        log.info("channel {} connected", ch);
        // this.registry.onConnected(flowId, ch);
        // System.err.println(ch);
        // this.writer.writeEvent(
        // (InetSocketAddress) ch.localAddress(),
        // (InetSocketAddress) ch.remoteAddress(),
        // TransportEvent.CONNECT);
        // sendKeepalive(ch);
      },
      err -> {
        log.debug("error opening connection: {}", err.getMessage());
        // this.registry.onError(flowId, err);
        // this.writer.writeEvent(
        // new InetSocketAddress(0),
        // remote,
        // TransportEvent.DISCONNECT);
      });

    // new SipServerHandlerInit(subject, sslctx, targetHost.getHost(),
    // targetHost.getPortOrDefault(remoteAddress.getPort()))

    // System.err.println(open);
    // TlsInfo tls = null;
    // String id = connect(target, null);
    // System.err.println(id);

  }

  private Single<Channel> connect(InetSocketAddress remoteAddress, ChannelHandler channelHandler) {

    try {

      Bootstrap b = new Bootstrap();

      SingleSubject<Channel> subject = SingleSubject.create();

      ChannelFuture ch =
        b
          .group(this.elg)
          .channel(NioSocketChannel.class)
          .option(ChannelOption.SO_REUSEADDR, true)
          .option(ChannelOption.AUTO_READ, true)
          .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
          // .option(ChannelOption.SO_TIMEOUT, 1000)
          .handler(channelHandler)
          .connect(remoteAddress);
      // .connect(remoteAddress, localAddress)
      ;

      ch.addListener(
        new GenericFutureListener<Future<Void>>() {

          @Override
          public void operationComplete(Future<Void> future) throws Exception {
            try {
              // throws if there is an error.
              future.get();
              subject.onSuccess(ch.channel());
            }
            catch (ExecutionException ex) {
              // inner error.
              subject.onError(ex.getCause());
            }
            catch (Throwable t) {
              subject.onError(t);
            }
          }

        });

      return subject;

    }
    catch (Throwable ex) {

      return SingleSubject.error(ex);

    }

  }

  @Override
  public void run() {

    this.elg = new NioEventLoopGroup(1);
    this.registry = new ClientStreamRegistry(eventBus);
    this.writer = new ChronicleMessageWriter(Paths.get("./rx"));

    eventBus.register(this);

    List<Service> services = new ArrayList<>();
    services.add(HttpServer.forPort(eventBus, 8088));

    // services.add(new MicronautService(ClientApplication.class, eventBus, this.registry));

    ServiceManager manager = new ServiceManager(services);

    System.err.println("starting listeners");
    // manager.addListener(this, MoreExecutors.directExecutor());
    manager.startAsync();
    manager.awaitHealthy();
    System.err.println("running!");
    manager.awaitStopped();

  }

}
