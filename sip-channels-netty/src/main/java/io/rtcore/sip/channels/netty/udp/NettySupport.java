package io.rtcore.sip.channels.netty.udp;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;

final class NettySupport {

  private NettySupport() {
  }

  public static InetSocketAddress defaultBindAddress() {
    return new InetSocketAddress(0);
  }

  public static CompletionStage<?> send(final DatagramChannel ch, final DatagramPacket packet) {
    final CompletableFuture<Object> f = new CompletableFuture<>();
    ch.writeAndFlush(packet)
      .addListener(future -> {
        try {
          f.complete(future.get());
        }
        catch (final Throwable ex) {
          f.completeExceptionally(ex);
        }
      });
    return f;
  }

  public static CompletionStage<?> send(final DatagramChannel ch, final InputStream frame, final InetSocketAddress target) {
    try {
      return send(ch, new DatagramPacket(ch.alloc().ioBuffer().writeBytes(frame.readAllBytes()), target));
    }
    catch (final IOException e) {
      return CompletableFuture.failedStage(e);
    }
  }

}
