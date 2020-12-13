package io.rtcore.sip.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import io.rtcore.sip.netty.datagram.UdpPacketHandler;
import io.rtcore.sip.proxy.chronicle.Payloads;
import io.rtcore.sip.proxy.chronicle.Payloads.Frame;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.RollCycles;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;

public class MessageReader {

  private final SingleChronicleQueue queue;
  private final ExcerptTailer tailer;

  public MessageReader(Path path, boolean readOnly, String tailerId) {
    this.queue =
      SingleChronicleQueueBuilder.binary(path)
        .rollCycle(RollCycles.HOURLY)
        .readOnly(readOnly)
        .build();
    this.tailer = queue.createTailer(tailerId);
  }

  public boolean read(UdpPacketHandler handler) {
    try (DocumentContext dc = tailer.readingDocument()) {
      if (!dc.isPresent()) {
        return false;
      }
      Frame f = Payloads.read(dc.wire().bytes().inputStream());
      handler.acceptUdpPacket(f.local, f.remote, ByteBuffer.wrap(f.payload));
      return true;
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

  public Frame read() {

    try (DocumentContext dc = tailer.readingDocument()) {
      if (!dc.isPresent()) {
        return null;
      }
      return Payloads.read(dc.wire().bytes().inputStream());
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }

  }

  public void resetPosition() {
    this.tailer.toStart();
  }

  public long currentIndex() {
    return this.tailer.index();
  }

}
