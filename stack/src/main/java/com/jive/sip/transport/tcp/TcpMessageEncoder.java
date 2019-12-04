package com.jive.sip.transport.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.jive.sip.message.api.SipMessage;
import com.jive.sip.processor.rfc3261.SipMessageManager;

public class TcpMessageEncoder extends MessageToByteEncoder<SipMessage>
{

  private final SipMessageManager manager;

  public TcpMessageEncoder(final SipMessageManager sipMessageManager)
  {
    this.manager = sipMessageManager;
  }

  @Override
  protected void encode(final ChannelHandlerContext ctx, final SipMessage msg, final ByteBuf out) throws Exception
  {
    out.writeBytes(this.manager.toBytes(msg));
  }

}
