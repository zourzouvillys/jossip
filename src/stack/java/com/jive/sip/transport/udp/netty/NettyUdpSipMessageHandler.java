package com.jive.sip.transport.udp.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

import lombok.extern.slf4j.Slf4j;

import com.jive.sip.base.api.RawMessage;
import com.jive.sip.message.api.SipMessage;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.parsers.core.ParseFailureException;
import com.jive.sip.processor.rfc3261.SipMessageManager;
import com.jive.sip.processor.rfc3261.parsing.RfcMessageParserBuilder;
import com.jive.sip.processor.rfc3261.parsing.RfcSipMessageParser;
import com.jive.sip.processor.rfc3261.parsing.SipMessageParseFailureException;
import com.jive.sip.transport.udp.UdpFlowId;
import com.jive.sip.transport.udp.UdpTransportListener;

@Slf4j
public class NettyUdpSipMessageHandler extends SimpleChannelInboundHandler<DatagramPacket>
{

  // always use RFC parser for now.
  private final RfcSipMessageParser parser = new RfcMessageParserBuilder().build();

  private final UdpTransportListener listener;

  private final NettyUdpListener transport;

  private final SipMessageManager manager;

  public NettyUdpSipMessageHandler(final SipMessageManager manager, final NettyUdpListener transport, final UdpTransportListener listener)
  {
    this.manager = manager;
    this.transport = transport;
    this.listener = listener;
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final DatagramPacket pkt) throws Exception
  {

    if (this.listener == null)
    {
      log.error("Received SIP message on channel without listener");
      return;
    }
    else if (pkt.sender().getPort() == 0)
    {
      log.debug("Invalid port in message from {}", pkt.sender());
      return;
    }

    final ByteBuf buffer = pkt.content();

    try
    {

      final UdpFlowId flow = this.transport.createFlowId(pkt.sender());

      if ((buffer.readableBytes() > 2) && ((buffer.getByte(buffer.readerIndex()) == 0) || (buffer.getByte(buffer.readerIndex()) == 1)))
      {
        // it's probably a STUN packet
        this.listener.onStunPacket(flow, pkt);
        return;
      }

      if ((buffer.readableBytes() < 4) || ((buffer.getByte(buffer.readerIndex()) == '\r') || (buffer.getByte(buffer.readerIndex()) == '\n')))
      {
        log.trace("Keepalive from {}", pkt.sender());
        this.listener.onKeepalive(flow, pkt.sender());
        return;
      }

      // some devices send "[ip]:[port]\r\n" in a UDP packet. no idea why or what it is, but let's drop it.

      // at this point, we push the work out of the netty handler.

      final SipMessage msg = parseSipMessage(pkt.sender(), buffer);

      if (msg == null)
      {
        // just CRLF
        return;
      }

      // we have the message, dispatch event.

      log.trace("Got SIP message: {} sending to {}", msg, this.listener);

      if (msg instanceof SipRequest)
      {
        this.listener.onSipRequestReceived(flow, pkt.sender(), (SipRequest) msg);
      }
      else if (msg instanceof SipResponse)
      {
        this.listener.onSipResponseReceived(flow, pkt.sender(), (SipResponse) msg);
      }
      else
      {
        log.error("Unknown message type: {}", msg.getClass());
        throw new RuntimeException("Unknown message type");
      }

    }
    catch (final SipMessageParseFailureException ex)
    {
      // just drop it. should log to OAM too -- TPZ
      log.info("Got invalid SIP message from {}: {}", pkt.sender(), ex.getMessage());
      this.listener.onInvalidSipMessageEvent(this.transport.createFlowId(pkt.sender()), pkt.sender());
    }
    catch (final Exception ex)
    {
      log.warn(String.format("Error processing UDP packet from %s", pkt.sender()), ex);
    }

  }

  private SipMessage parseSipMessage(final InetSocketAddress sender, final ByteBuf buffer) throws SipMessageParseFailureException
  {


    try
    {
      final RawMessage msg = this.parser.parse(buffer.nioBuffer());
      if (msg == null)
      {
        return null;
      }
      return this.manager.convert(msg);
    }
    catch (final ParseFailureException e)
    {
      log.info("Exception caught parsing message from {}: {}", sender, e.getMessage());
      return null;
    }


  }


  @Override
  public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception
  {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception
  {
    log.warn("Failed to parse message", cause);
    // We don't close the channel because we can keep serving requests.
  }

  /**
   *
   */
  @Override
  public void channelInactive(final ChannelHandlerContext ctx) throws Exception
  {
    log.info("Channel Inactive");
    super.channelInactive(ctx);
  }


}