package io.rtcore.sip.channels.netty.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Supplier;

import org.reactivestreams.FlowAdapters;

import com.google.common.base.Verify;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DatagramPacketEncoder;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.subscribers.ResourceSubscriber;
import io.rtcore.sip.channels.SipAttributes;
import io.rtcore.sip.channels.SipTransport;
import io.rtcore.sip.channels.SipUdpSocket;
import io.rtcore.sip.channels.SipWirePacket;
import io.rtcore.sip.channels.SipWireProducer;
import io.rtcore.sip.channels.netty.codec.SipObjectEncoder;
import io.rtcore.sip.channels.netty.internal.RefCounted;
import io.rtcore.sip.common.ImmutableHostPort;
import io.rtcore.sip.common.iana.StandardSipTransportName;
import io.rtcore.sip.message.message.SipMessage;
import io.rtcore.sip.message.processor.rfc3261.RfcSipMessageManager;
import io.rtcore.sip.message.processor.rfc3261.parsing.SipMessageParseFailureException;

public class NettyUdpChannel extends SimpleChannelInboundHandler<DatagramPacket> implements SipUdpSocket {

  // todo: this is a hack, fix.
  private static final RefCounted<NioEventLoopGroup> defaultEventLoopGroup =
      RefCounted.create(
        NioEventLoopGroup::new,
        NioEventLoopGroup::shutdownGracefully);

  private final DatagramChannel ch;
  private final Demand demand = new Demand();
  private final LinkedTransferQueue<DatagramPacket> rx = new LinkedTransferQueue<>();

  // all current UdpFlow mappings, ref counted optionally with expiry/caching.
  private final Map<InetSocketAddress, UdpFlow> flows = new HashMap<>();

  private Subscriber<? super SipWireProducer> subscriber;

  class UdpChannelInitializer extends ChannelInitializer<DatagramChannel> {

    @Override
    public void initChannel(final DatagramChannel channel) {
      channel.pipeline().addLast(new DatagramPacketEncoder<>(new SipObjectEncoder()));
      // channel.pipeline().addLast(new DatagramPacketDecoder<>(new SipObjectDecoder()));
      channel.pipeline().addLast("handler", NettyUdpChannel.this);
    }

  }

  NettyUdpChannel(final Bootstrap bootstrap, final Supplier<InetSocketAddress> bindAddress) {

    Objects.requireNonNull(bindAddress, "bindAddress");

    if (bootstrap.config().group() == null) {
      bootstrap.group(defaultEventLoopGroup.get());
    }

    if (bootstrap.config().channelFactory() == null) {
      bootstrap.channelFactory(new ReflectiveChannelFactory<>(NioDatagramChannel.class));
    }

    this.ch =
        (DatagramChannel) bootstrap
        .handler(new UdpChannelInitializer())
        // we never auto-close on write failure, as a single write failure to one destinasiton does
        // not imply permanent failure.
        .option(ChannelOption.AUTO_CLOSE, false)
        // no automatic reading, we only read based on requests from subscribers.
        .option(ChannelOption.AUTO_READ, false)
        //
        .bind(bindAddress.get())
        .syncUninterruptibly()
        .channel();
  }

  /**
   *
   */

  private final class UdpFlow {

    private final InetSocketAddress target;

    UdpFlow(final InetSocketAddress target) {
      this.target = target;
    }

    public ResourceSubscriber<SipMessage> writer() {

      return new ResourceSubscriber<>() {

        @Override
        public void onStart() {
          // add(Schedulers.single().scheduleDirect(() -> println("Time!"), 2, SECONDS));
          // todo: only request if we have some buffer space and flow is possible.
          this.request(1);
        }

        @Override
        public void onNext(@NonNull final SipMessage message) {
          // only allow one at a time
          // todo: don't request more until buffer available...
          NettyUdpChannel.this.ch.writeAndFlush(new DefaultAddressedEnvelope<>(message, UdpFlow.this.target))
          .addListener(f -> this.request(1));
        }

        @Override
        public void onError(final Throwable t) {
          t.printStackTrace();
          this.dispose();
        }

        @Override
        public void onComplete() {
          System.err.println("completed");
        }

      };

    }

  }

  /**
   * write a writer
   */

  @Override
  public void send(final InetSocketAddress target, final Flow.Publisher<SipMessage> msg) {
    final UdpFlow flow = this.flows.computeIfAbsent(target, UdpFlow::new);
    Flowable.fromPublisher(FlowAdapters.toPublisher(msg)).subscribeWith(flow.writer());
  }

  /**
   *
   */

  public InetSocketAddress localAddress() {
    return this.ch.localAddress();
  }

  /**
   *
   */

  public void close() {
    this.ch.close();
  }

  /**
   * a packet was received from the network. it may be any form of UDP payload (including zero
   * bytes) and at this point we have not decoded it.
   *
   * a common optimization is to perform a lightweight scan for the branch parameter in the top Via
   * header, and pass the buffer to whatever is responsible for it.
   */

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final DatagramPacket msg) throws Exception {
    this.rx.add(msg.retain());
  }

  /*
   * dispatch the packets
   */

  @Override
  public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {

    if (this.demand.tryDecrement()) {

      this.subscriber.onNext((SipWireProducer) () -> {

        final DatagramPacket pkt = this.rx.poll();

        if (pkt == null) {
          return null;
        }

        try {

          final SipMessage msg = RfcSipMessageManager.defaultInstance().parseMessage(pkt.content().nioBuffer());

          final SipAttributes.Builder ab =
              SipAttributes.newBuilder()
              .set(SipTransport.ATTR_TRANSPORT, StandardSipTransportName.UDP)
              .set(SipTransport.ATTR_LOCAL_ADDR, pkt.recipient())
              .set(SipTransport.ATTR_REMOTE_ADDR, pkt.sender());

          msg.topVia().ifPresent(via -> {
            ab.set(SipTransport.ATTR_SENT_BY, ImmutableHostPort.copyOf(via.sentBy()));
            // set the branch ID if there is one.
            via.branchWithoutCookie().ifPresent(branch -> ab.set(SipTransport.ATTR_BRANCH_ID, branch));
          });

          return SipWirePacket.of(msg, ab.build());

        }
        catch (final SipMessageParseFailureException ex) {

          // there was a problem parsing the message - but this isn't fatal.

          return new SipWirePacket() {

            @Override
            public void close() throws IOException {
            }

            @Override
            public SipMessage payload() {
              return null;
            }

            @Override
            public String toString() {
              return ex.getMessage();
            }

          };

        }
        finally {

          pkt.release();

        }

      });

    }

    if (!this.demand.isFulfilled()) {
      // still more outstanding demand.
      ctx.read();
    }

    super.channelReadComplete(ctx);

  }

  @Override
  public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
    // todo: handle?
    super.userEventTriggered(ctx, evt);
  }

  @Override
  public void channelWritabilityChanged(final ChannelHandlerContext ctx) throws Exception {
    if (!ctx.channel().isWritable()) {
      // todo: no longer able to write without buffering, apply backpressure.
    }
    else {
      // todo: not restricted anymore, remove any backpressure.
    }
    super.channelWritabilityChanged(ctx);
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
    // todo: handle
    super.exceptionCaught(ctx, cause);
  }

  /**
   * add a channel reader which is notified when there are new packets available.
   */

  @Override
  public void subscribe(final Subscriber<? super SipWireProducer> subscriber) {

    Verify.verify(this.subscriber == null, "subscriber already set");

    this.subscriber = subscriber;

    this.subscriber.onSubscribe(new Subscription() {

      @Override
      public void request(final long n) {
        if (NettyUdpChannel.this.demand.increase(n)) {
          NettyUdpChannel.this.ch.read();
        }
      }

      @Override
      public void cancel() {
        NettyUdpChannel.this.ch.close()
        .addListener(f -> {
          subscriber.onComplete();
        });
      }

    });

  }

}