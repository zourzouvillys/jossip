package io.rtcore.sip.proxy.cmd.gateway;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Paths;

import com.google.common.io.ByteStreams;
import com.google.common.net.HostAndPort;

import io.rtcore.sip.proxy.chronicle.ChronicleMessageWriter;
import io.rtcore.sip.proxy.client.SipClientManager;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "txmit", description = "submit tx command")
public class TransmitCommand implements Runnable {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TransmitCommand.class);

  @Option(names = { "--txpath" }, required = true)
  private String txpath;

  @Option(names = { "--target" }, required = true)
  private String target;

  @Parameters(index = "0", defaultValue = "-")
  private String source = "-";

  @Override
  public void run() {

    ChronicleMessageWriter writer = new ChronicleMessageWriter(Paths.get(txpath));
    SipClientManager client = new SipClientManager(writer);

    HostAndPort thap = HostAndPort.fromString(target);

    byte[] data;
    try {

      data = ByteStreams.toByteArray(getSource());
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }

    client.send(new InetSocketAddress(thap.getHost(), thap.getPortOrDefault(5060)), ByteBuffer.wrap(data));

  }

  private InputStream getSource() throws FileNotFoundException {
    if (source.equals("-"))
      return System.in;
    return new FileInputStream(this.source);
  }

}
