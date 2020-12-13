package io.rtcore.sip.proxy;

import io.rtcore.sip.proxy.cmd.gateway.GatewayCommand;
import io.rtcore.sip.proxy.cmd.gateway.TransmitCommand;
import io.rtcore.sip.proxy.plugins.aws.kinesis.KinesisReader;
import io.rtcore.sip.proxy.plugins.postgres.PostgresPlugin;
import io.rtcore.sip.proxy.plugins.rocksdb.RocksDbPlugin;
import io.rtcore.sip.proxy.transport.datagram.DatagramReceive;
import io.rtcore.sip.proxy.transport.stream.client.StreamConnectCommand;
import io.rtcore.sip.proxy.transport.stream.server.StreamListenCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * main server runtime entry point.
 */

@Command(
    subcommands = {
      DatagramReceive.class,
      StreamListenCommand.class,
      StreamConnectCommand.class,
      GatewayCommand.class,
      DumpMessagesCommand.class,
      TransmitCommand.class,
      PostgresPlugin.class,
      RocksDbPlugin.class,
      //
      KinesisReader.Command.class,
      //
    })

public class EntryPoint {

  public static void main(String[] args) throws Exception {
    System.exit(new CommandLine(new EntryPoint()).execute(args));
  }

}
