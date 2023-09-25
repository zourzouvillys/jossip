package io.rtcore.gateway;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.common.net.InetAddresses;
import com.google.common.util.concurrent.ServiceManager;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.ServerBuilder;
import io.rtcore.gateway.engine.grpc.SipGrpcServer;
import io.rtcore.gateway.engine.grpc.SipServerBase;
import io.rtcore.gateway.engine.grpc.client.SipGrpcClient;
import io.rtcore.gateway.engine.sip.NetworkSegment;
import io.rtcore.gateway.udp.SipDatagramClientManager;
import io.rtcore.gateway.udp.SipDatagramSocket;
import io.rtcore.sip.common.HostPort;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.frame.SipRequestFrame;
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

  @Command
  public int client() {

    Channel ch = ManagedChannelBuilder.forAddress("localhost", 8881)
        .usePlaintext()
        .directExecutor()
        .maxInboundMessageSize(1024 * 1024 * 1024)
        .maxInboundMetadataSize(1024 * 1024 * 1024)
        .build();

    SipGrpcClient client = SipGrpcClient.create(ch);

    // all the headers to add.
    List<SipHeaderLine> headers = new ArrayList<>();

    client
        .exchange(SipRequestFrame.of(SipMethods.REGISTER, URI.create("sip:invalid"), headers))
        .blockingForEach(System.out::println);

    return 0;

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

    final NetworkSegment segment = new NetworkSegment(ctx -> {
      System.err.println("got request" + ctx);
    });

    SipDatagramSocket udp = SipDatagramSocket.create(b -> b
        .bindAddress(new InetSocketAddress(InetAddresses.forString("0.0.0.0"), 0))
        .messageHandler(segment.new DatagramMessageReceiverAdapter()))
        .bindNow();

    SipDatagramClientManager client = new SipDatagramClientManager(segment, udp);

    final ServiceManager mgr = new ServiceManager(
        List.of(
            new SipGrpcServer(
                ServerBuilder.forPort(8881)
                    .directExecutor()
                    .addService(new SipServerBase(client)))));

    // new HttpSipServer(new EmptyExternalSipServer(), 8888)

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
    System.exit(
        new CommandLine(new EntryPoint()).registerConverter(HostPort.class, new HostPortConverter()).execute(args));
  }

}
