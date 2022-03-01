package io.rtcore.sip.channels.netty.udp;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;

import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Publisher;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.reactivex.rxjava3.core.Flowable;
import io.rtcore.sip.message.message.SipMessage;

public class TestUserAgent {

  private final NettyUdpChannel server;
  private final TestPacketSubscriber rx;

  public TestUserAgent(final NioEventLoopGroup group) {

    this.server =
        new NettyUdpServerBuilder()
        .eventLoop(group)
        .channel(NioDatagramChannel.class)
        .bindNow(new InetSocketAddress(loopbackAddress(), 0));

    this.rx = new TestPacketSubscriber();

    // receive into subscriber which just stores.
    this.server.subscribe(this.rx);

  }

  public List<SipMessage> packets() {
    return this.rx.packets();
  }

  public InetSocketAddress localAddress() {
    return this.server.localAddress();
  }

  public void write(final InetSocketAddress target, final SipMessage msg) {
    this.write(target, Flowable.just(msg));
  }

  public void write(final InetSocketAddress target, final Publisher<SipMessage> msg) {
    this.server.send(target, FlowAdapters.toFlowPublisher(msg));
  }

  public void close() {
    this.server.close();
  }

  private static final InetAddress loopbackAddress() {

    try {
      return NetworkInterface.networkInterfaces()
          .filter(t -> {
            try {
              return t.isLoopback() && t.isUp() && !t.isPointToPoint();
            }
            catch (final SocketException e) {
              // TODO Auto-generated catch block
              throw new RuntimeException(e);
            }
          })
          .flatMap(NetworkInterface::inetAddresses)
          .filter(e -> e instanceof Inet4Address)
          .findFirst()
          .orElseThrow();
    }
    catch (final SocketException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }

  }

}
