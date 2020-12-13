package io.rtcore.sip.proxy.chronicle;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.time.Instant;

import io.rtcore.sip.proxy.MessageWriter;
import io.rtcore.sip.proxy.chronicle.Payloads.Frame;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.RollCycles;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;

public class ChronicleMessageWriter implements MessageWriter {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ChronicleMessageWriter.class);

  private final SingleChronicleQueue queue;

  public ChronicleMessageWriter(Path path) {
    this.queue = SingleChronicleQueueBuilder.binary(path).rollCycle(RollCycles.HOURLY).build();
  }

  @Override
  public void write(InetSocketAddress local, InetSocketAddress remote, ByteBuffer buffer) {

    log.debug("writting buffer from {} -> {}, {} bytes", remote, local, buffer.remaining());

    Frame frame = new Payloads.Frame();
    frame.time = Instant.now();
    frame.proto = "UDP";
    frame.local = local;
    frame.remote = remote;
    frame.payload = new byte[buffer.remaining()];
    buffer.get(frame.payload);
    
    System.err.println(new String(frame.payload));

    this.write(frame);

  }

  @Override
  public void writeEvent(InetSocketAddress local, InetSocketAddress remote, TransportEvent eventType) {
    ExcerptAppender appender = queue.acquireAppender();
    try (DocumentContext dc = appender.writingDocument()) {
      throw new IllegalArgumentException("not implemented");
    }
  }

  public void write(Frame frame) {
    ExcerptAppender appender = queue.acquireAppender();
    try (DocumentContext dc = appender.writingDocument()) {
      Payloads.write(dc.wire().bytes().outputStream(), frame);
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

}
