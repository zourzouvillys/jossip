package io.rtcore.sip.proxy.transport.datagram;

import java.nio.file.Path;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractExecutionThreadService;

import io.rtcore.sip.netty.datagram.UdpReceiver;
import io.rtcore.sip.proxy.MessageReader;
import io.rtcore.sip.proxy.MessageWriter;
import io.rtcore.sip.proxy.chronicle.ChronicleMessageWriter;
import net.openhft.chronicle.threads.Pauser;

public class DatagramListenerService extends AbstractExecutionThreadService {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DatagramListenerService.class);

  private final MessageWriter writer;
  private final MessageReader reader;
  private final UdpReceiver ch;
  private final Pauser pauser;

  public DatagramListenerService(EventBus eventBus, DatagramListenerSpec spec, Path rxpath, Path txpath) {
    this.writer = new ChronicleMessageWriter(rxpath);
    this.reader = new MessageReader(txpath, false, "transmitter");
    this.ch = UdpReceiver.bind(spec.bindAddress(), spec.externalAddress());
    this.pauser = Pauser.balanced();
  } 

  @Override
  protected void run() throws Exception {

    while (this.isRunning()) {

      int count = ch.read(writer::write, 1024);

      while (reader.read(ch::write)) {
        count++;
      }

      if (count == 0) {
        pauser.pause();
      }
      else {
        log.debug("processed {} work items", count);
        pauser.unpause();
      }

    }
  }

}
