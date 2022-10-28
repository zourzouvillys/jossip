package io.rtcore.sip.channels.netty.udp;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.rtcore.sip.channels.api.SipFrame;

@Sharable
public class DatagramSipFrameDecoder extends MessageToMessageDecoder<DatagramPacket> {

  private final SipFrameDecoder decoder;

  public DatagramSipFrameDecoder(SipFrameDecoder decoder) {
    this.decoder = checkNotNull(decoder, "decoder");
  }

  @Override
  public boolean acceptInboundMessage(Object msg) throws Exception {
    if (msg instanceof DatagramPacket) {
      return (((DatagramPacket) msg).content()) instanceof ByteBuf;
    }
    return false;
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
    SipFrame frame = this.decoder.decode(msg.content());
    if (frame == null) {
      out.add(msg.retain());
    }
    else {
      out.add(new SipDatagramPacket(frame, msg.recipient(), msg.sender()));
    }
  }

}
