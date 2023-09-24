package io.rtcore.gateway;

import java.util.List;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.common.util.concurrent.ServiceManager;

import io.grpc.ServerBuilder;
import io.rtcore.gateway.engine.grpc.SipGrpcServer;
import io.rtcore.gateway.engine.grpc.SipServerBase;
import io.rtcore.gateway.engine.http.server.HttpSipServer;
import io.rtcore.sip.common.HostPort;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Parameters;

@Command(name = "Protocol Gateway", mixinStandardHelpOptions = true)
public class EntryPoint {

  static {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
  }

  static class HostPortConverter implements ITypeConverter<HostPort> {

    @Override
    public HostPort convert(final String value) throws Exception {
      return HostPort.fromString(value);
    }

  }

  /**
   *
   */

  @Command
  public int server(@Parameters(paramLabel = "SIP-PROXY") final HostPort targetSip) {

    // final Server server =
    // DaggerServer.builder()
    // .restModule(new RestModule(1188))
    // .sipEngineModule(new
    // SipEngineModule(SipRoute.tcp(InetAddresses.forString(targetSip.host().toAddrString()),
    // targetSip.port().orElse(5060))))
    // .build();

    // final ServiceManager mgr = server.serviceManager();

    final ServiceManager mgr =
      new ServiceManager(
        List.of(
          new SipGrpcServer(
            ServerBuilder.forPort(8881)
              .directExecutor()
              .addService(new SipServerBase())),
          new HttpSipServer(new EmptyExternalSipServer(), 8888)));

    mgr.startAsync();
    mgr.awaitHealthy();
    mgr.awaitStopped();
    return 0;

  }

  /**
   *
   */

  @Command
  public int healthcheck() {
    return 0;
  }

  public static void main(final String[] args) {
    System.exit(new CommandLine(new EntryPoint()).registerConverter(HostPort.class, new HostPortConverter()).execute(args));
  }

}
