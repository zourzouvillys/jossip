package io.rtcore.sip.proxy.transport.datagram;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
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

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * main network runtime entry point.
 */

@Command(name = "datagram:bind", description = "send/receive UDP traffic on specified port.")
public class DatagramReceive extends ServiceManager.Listener implements Callable<Integer> {

  /**
   * 
   */

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DatagramReceive.class);

  /**
   * 
   */

  @Option(names = { "--rx" }, description = "rx packet queue path")
  String rxpath = "./rx";

  /**
   * 
   */

  @Option(names = { "--tx" }, description = "tx packet queue path")
  String txpath = "./tx";

  /**
   * 
   */
  
  @ArgGroup(exclusive = false, multiplicity = "0..*")
  List<Composite> composites = new ArrayList<>();

  /**
   * 
   */

  static class Composite implements DatagramListenerSpec {

    @Parameters(index = "0")
    String listen = "0.0.0.0:5060";

    @Parameters(index = "1", arity = "0..1")
    String external;

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

    @Override
    public String toString() {
      String protocol = "udp";
      if (this.bindAddress().equals(this.externalAddress())) {
        return String.format("%s:%s", protocol, this.bindAddress());
      }
      return String.format("%s:%s (%s)", protocol, this.bindAddress(), this.externalAddress());
    }

    public DatagramListenerSpec toSpec() {
      return this;
    }

  }

  @Override
  public Integer call() {
    try {
      if (composites.isEmpty()) {
        // default.
        composites.add(new Composite());
      }
      return execute();
    }
    catch (InterruptedException | IOException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

  @Override
  public void healthy() {
    log.info("all services are healthy");
  }

  @Override
  public void stopped() {
    log.info("all services are stopped");
  }

  @Override
  public void failure(Service service) {
    log.error("service [{}] failure: {}", service, service.failureCause().getMessage());
  }

  public int execute() throws InterruptedException, IOException {

    //
    EventBus eventBus = new EventBus((ex, ctx) -> {
      log.error("error with {}: {}", ctx, ex.getMessage(), ex);
    });

    List<Service> services =
      composites.stream()
        .map(c -> c.toSpec())
        .map(spec -> new DatagramListenerService(eventBus, spec, Paths.get(rxpath), Paths.get(txpath)))
        .collect(Collectors.toList());

    // services.add(HttpServer.forPort(eventBus, 8088));

    ServiceManager manager = new ServiceManager(services);
    log.info("starting listeners");
    manager.addListener(this, MoreExecutors.directExecutor());
    manager.startAsync();
    manager.awaitHealthy();
    log.info("running!");
    manager.awaitStopped();
    return 0;

  }

}
