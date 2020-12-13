package io.rtcore.sip.proxy.transport.stream.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.google.common.util.concurrent.ServiceManager.Listener;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.rtcore.sip.proxy.http.HttpServer;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * main network runtime entry point.
 */

@Command(name = "stream:listen")
public class StreamListenCommand extends Listener implements Callable<Integer> {

  /**
   * 
   */

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StreamListenCommand.class);
  private static final EventBus eventBus = new EventBus();

  /**
   * 
   */

  @ArgGroup(exclusive = false, multiplicity = "0..*")
  List<Composite> composites = new ArrayList<>();

  static class Composite implements StreamListenerSpec {

    @Parameters(index = "0")
    String listen = "0.0.0.0:5060";

    @Parameters(index = "1", arity = "0..1")
    String external;

    @Option(names = { "--tls" })
    boolean tls;

    @Option(names = { "--proxyProtocol" })
    boolean proxyProtocol;

    @Override
    public boolean tls() {
      return this.tls;
    }

    @Override
    public boolean proxyProtocol() {
      return this.proxyProtocol;
    }

    @Override
    public InetSocketAddress bindAddress() {
      HostAndPort target = HostAndPort.fromString(listen).withDefaultPort(5060);
      return new InetSocketAddress(InetAddresses.forUriString(target.getHost()), target.getPort());
    }

    @Override
    public InetSocketAddress externalAddress() {
      if ((external == null) || Strings.isNullOrEmpty(external)) {
        return bindAddress();
      }
      HostAndPort target = HostAndPort.fromString(external).withDefaultPort(bindAddress().getPort());
      return new InetSocketAddress(InetAddresses.forUriString(target.getHost()), target.getPort());
    }

    public StreamListenerSpec toSpec() {
      return this;
    }

    @Override
    public String toString() {
      String protocol =
        tls ? "tls"
            : "tcp";
      if (this.bindAddress().equals(this.externalAddress())) {
        return String.format("%s:%s",
          protocol,
          this.bindAddress());
      }
      return String.format("%s:%s (%s)", protocol, this.bindAddress(), this.externalAddress());
    }

  }

  private EventLoopGroup elg;
  private Channel streamListener;

  @Override
  public Integer call() {

    if (composites.isEmpty()) {
      composites.add(new Composite());
    }

    List<Service> services =
      composites.stream()
        .map(c -> c.toSpec())
        .map(spec -> new StreamListenerService(eventBus, spec))
        .collect(Collectors.toList());

    services.add(HttpServer.forPort(eventBus, 8088));

    ServiceManager manager = new ServiceManager(services);

    System.err.println("starting listeners");

    manager.addListener(this, MoreExecutors.directExecutor());

    manager.startAsync();
    manager.awaitHealthy();

    System.err.println("running!");

    manager.awaitStopped();

    return 0;

  }

  @Override
  public void healthy() {
    System.err.println("healthy");
  }

  @Override
  public void stopped() {
    System.err.println("Stopped");
  }

  @Override
  public void failure(Service service) {
    System.err.println("service failed");
  }

}
