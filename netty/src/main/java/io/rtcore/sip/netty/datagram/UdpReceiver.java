package io.rtcore.sip.netty.datagram;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import io.netty.buffer.ByteBuf;

public class UdpReceiver {

  private final DatagramChannel server;
  private final ByteBuffer buffer;
  private final InetSocketAddress localAddress;
  private final InetSocketAddress actualAddress;

  UdpReceiver(InetSocketAddress socketAddress, int maxSize) {
    this(socketAddress, socketAddress, maxSize);
  }

  UdpReceiver(InetSocketAddress socketAddress, InetSocketAddress actualAddress, int maxSize) {
    this.buffer = ByteBuffer.allocateDirect(maxSize);
    this.actualAddress = actualAddress;
    try {
      this.server = DatagramChannel.open();
      this.server.configureBlocking(false);
      this.server.bind(socketAddress);
      this.localAddress = (InetSocketAddress) this.server.getLocalAddress();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public SelectionKey register(Selector selector) {
    try {
      return this.server.register(selector, SelectionKey.OP_READ, this);
    }
    catch (ClosedChannelException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

  /**
   * reads a batch of messages if any are available. returns true if any have been read.
   */

  public int read(UdpPacketHandler b, int max) {

    int count = 0;

    try {

      do {
        buffer.clear();
        SocketAddress sender = server.receive(buffer);
        if (sender == null) {
          break;
        }
        buffer.flip();
        b.acceptUdpPacket(this.actualAddress, (InetSocketAddress) sender, buffer);
      }
      while (++count < max);

    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }

    return count;

  }

  public void write(InetSocketAddress source, InetSocketAddress target, ByteBuffer data) {
    try {
      server.send(data, target);
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

  public void write(InetSocketAddress target, ByteBuf data) {
    try {
      server.send(data.nioBuffer(), target);
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

  public static UdpReceiver bind(InetSocketAddress socketAddress) {
    return new UdpReceiver(socketAddress, socketAddress, 1024 * 32);
  }

  public static UdpReceiver bind(InetSocketAddress socketAddress, InetSocketAddress actualAddress) {
    return new UdpReceiver(socketAddress, actualAddress, 1024 * 32);
  }

}
