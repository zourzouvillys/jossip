package io.rtcore.sip.netty.datagram;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.function.BiConsumer;

public final class SipDatagramChannel {

  private final DatagramChannel server;

  // we only use a single byte buffer for incoming packets, the synchronous handler must copy to
  // content if it wishes to keep the buffer around.
  private final ByteBuffer buffer;

  private final InetSocketAddress localAddress;

  private SipDatagramChannel(InetSocketAddress socketAddress, int maxSize) {
    this.buffer = ByteBuffer.allocateDirect(maxSize);
    try {
      this.server = DatagramChannel.open();
      this.server.configureBlocking(false);
      this.server.bind(socketAddress);
      this.localAddress = (InetSocketAddress) this.server.getLocalAddress();
    }
    catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  /**
   * register a selector for reads.
   */

  public SelectionKey register(Selector selector) {
    try {
      return this.server.register(selector, SelectionKey.OP_READ, this);
    }
    catch (ClosedChannelException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * reads a batch of messages if any are available. returns tje number of messages read.
   */

  public int read(BiConsumer<InetSocketAddress, ByteBuffer> b, int max) {
    int count = 0;
    try {
      do {
        buffer.clear();
        SocketAddress sender = server.receive(buffer);
        if (sender == null) {
          break;
        }
        buffer.flip();
        b.accept((InetSocketAddress) sender, buffer);
      }
      while (++count < max);
    }
    catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
    return count;
  }

  public void write(SocketAddress target, ByteBuffer data) {
    try {
      server.send(data, target);
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static SipDatagramChannel bind(InetSocketAddress socketAddress) {
    return new SipDatagramChannel(socketAddress, 1024 * 32);
  }

  public InetSocketAddress localAddress() {
    return this.localAddress;
  }

}
