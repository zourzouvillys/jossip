package io.rtcore.gateway.udp;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.codec.DatagramPacketEncoder;
import io.rtcore.sip.netty.codec.SipObjectEncoder;
import io.rtcore.sip.netty.codec.udp.SipDatagramDecoder;
import io.rtcore.sip.netty.codec.udp.SipDatagramPacket;
import io.rtcore.sip.netty.codec.udp.SipFrameDecoder;

/**
 * netty initializer for UDP socket.
 */

class SipDatagramChannelInitializer extends ChannelInitializer<DatagramChannel> {

  private static final Logger LOG = LoggerFactory.getLogger(SipDatagramChannelInitializer.class);

  private final SipDatagramMessageHandler receiver;
  private final SipDatagramSocket socket;

  /**
   * listener which receives the frames.
   */

  SipDatagramChannelInitializer(final SipDatagramSocket socket, final SipDatagramSocketConfig config) {
    this.socket = socket;
    this.receiver = Objects.requireNonNull(config.messageHandler());
  }

  /**
   * the handler which gets added to the channel, to deliver the packets to the handler.
   */

  private class PacketHandler extends SimpleChannelInboundHandler<SipDatagramPacket> {

    PacketHandler() {
      super(SipDatagramPacket.class, true);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final SipDatagramPacket pkt) throws Exception {
      try {
        SipDatagramChannelInitializer.this.receiver.receiveDatagramFrame(SipDatagramChannelInitializer.this.socket, pkt);
      }
      catch (final Exception ex) {
        // it is an error for the handler to throw an exception.
        LOG.error("datagram handler {} threw unexpected error", SipDatagramChannelInitializer.this.receiver, ex);
      }
    }

  }

  @Override
  public void initChannel(final DatagramChannel channel) {

    final ChannelPipeline p = channel.pipeline();

    // decode frames in whole units
    p.addLast(new SipDatagramDecoder(new SipFrameDecoder()));

    // encode SipFrame or SipMessage writes.
    p.addLast(new DatagramPacketEncoder<>(new SipObjectEncoder()));

    // handler which delivers to the receiver.
    p.addLast(new PacketHandler());

  }
}
