package io.rtcore.sip.channels.netty.udp;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.hash.Hashing;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DatagramPacketEncoder;
import io.netty.util.HashedWheelTimer;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipClientExchange;
import io.rtcore.sip.channels.connection.SipConnections;
import io.rtcore.sip.channels.netty.ClientBranchId;
import io.rtcore.sip.channels.netty.internal.NettySharedLoop;
import io.rtcore.sip.channels.netty.tcp.NettyUtils;
import io.rtcore.sip.common.HostPort;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.frame.SipFrame;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.message.message.api.ViaProtocol;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.netty.codec.SipObjectEncoder;
import io.rtcore.sip.netty.codec.udp.DatagramSipFrameDecoder;
import io.rtcore.sip.netty.codec.udp.SipDatagramPacket;
import io.rtcore.sip.netty.codec.udp.SipFrameDecoder;

public class NettyUdpSocket {

  private static final Logger logger = LoggerFactory.getLogger(NettyUdpSocket.class);
  private final HashedWheelTimer timer = new HashedWheelTimer(50, TimeUnit.MILLISECONDS);

  private final long keyId = System.currentTimeMillis();
  private final AtomicLong sequences = new AtomicLong(1);

  private final ClientBranchListener listener;

  private class UdpChannelInitializer extends ChannelInitializer<DatagramChannel> {

    @Override
    public void initChannel(final DatagramChannel channel) {

      ChannelPipeline p = channel.pipeline();

      p.addLast(new DatagramSipFrameDecoder(new SipFrameDecoder()));
      p.addLast(new DatagramPacketEncoder<>(new SipObjectEncoder()));

      p.addLast(new SimpleChannelInboundHandler<SipDatagramPacket>(SipDatagramPacket.class, true) {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, SipDatagramPacket pkt) throws Exception {
          try {
            listener.accept(pkt);
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }

      });

    }
  }

  private final DatagramChannel ch;

  NettyUdpSocket(EventLoopGroup sharedLoop, InetSocketAddress bindAddress, Consumer<SipDatagramRequest> receiver) {

    this.listener = new ClientBranchListener(receiver);

    Bootstrap bootstrap = new Bootstrap();

    Objects.requireNonNull(bindAddress, "bindAddress");

    if (bootstrap.config().group() == null) {
      bootstrap.group(NettySharedLoop.allocate());
    }

    if (bootstrap.config().channelFactory() == null) {
      bootstrap.channelFactory(new ReflectiveChannelFactory<>(NioDatagramChannel.class));
    }

    this.ch =
      (DatagramChannel) bootstrap
        .handler(new UdpChannelInitializer())
        // we never auto-close on write failure, as a single write failure to one does
        // not imply permanent failure.
        .option(ChannelOption.AUTO_CLOSE, false)
        .option(ChannelOption.AUTO_READ, true)

        //
        .bind(bindAddress)
        .syncUninterruptibly()
        .channel();

  }

  public static NettyUdpSocket create(EventLoopGroup sharedLoop, InetSocketAddress bindAddress, Consumer<SipDatagramRequest> receiver) {
    return new NettyUdpSocket(sharedLoop, bindAddress, receiver);
  }

  public void close() {
    this.ch.close();
  }

  public CompletionStage<?> send(InetSocketAddress recipient, SipFrame frame) {
    return NettyUtils.toCompletableFuture(ch.writeAndFlush(new DefaultAddressedEnvelope<>(frame, recipient)));
  }

  private ClientBranchId makeKey(SipRequestFrame req, SipAttributes attributes) {

    long seqId = sequences.getAndIncrement();

    String key =
      Hashing.farmHashFingerprint64()
        .newHasher()
        .putLong(keyId)
        .putLong(ThreadLocalRandom.current().nextLong())
        .putLong(seqId)
        .hash()
        .toString();

    String branchId = String.format("%s-%06x", key, seqId);

    return new ClientBranchId(
      attributes.getOrDefault(SipConnections.ATTR_SENT_BY, HostPort.fromHost("invalid")),
      req.initialLine().method(),
      branchId);

  }

  public SipClientExchange exchange(InetSocketAddress recipient, SipRequestFrame req, SipAttributes attributes) {

    ClientBranchId branchId = makeKey(req, attributes);

    SipHeaderLine topVia =
      StandardSipHeaders.VIA
        .ofLine(new Via(
          ViaProtocol.UDP,
          branchId.sentBy(),
          DefaultParameters.of()
            .withParameter("rport")
            .withToken("branch", "z9hG4bK" + branchId.branchId()))
          .encode());

    req = req.withHeaderLines(Iterables.concat(List.of(topVia), req.headerLines()));

    return new SipDatagramClientExchange(this, recipient, req, branchId);

  }

  public ClientBranchListener listener() {
    return this.listener;
  }

  /**
   * transmit a frame to a recipient until cancelled following a timeout pattern.
   * 
   * @return a runnable which should be called to cancel the retransmissions.
   * 
   */

  Runnable transmit(InetSocketAddress recipient, SipFrame frame, Iterator<Duration> retransmit) {

    logger.info("transmitting to {}", recipient);

    this.send(recipient, frame)
      .handle((res, err) -> {
        //
        return null;
      });

    // todo: actually retransmit...

    return () -> {

      // timeout.cancel();

    };

  }

}
