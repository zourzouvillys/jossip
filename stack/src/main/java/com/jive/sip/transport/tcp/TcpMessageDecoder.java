package com.jive.sip.transport.tcp;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

import com.jive.sip.base.api.RawMessage;
import com.jive.sip.message.api.SipMessage;
import com.jive.sip.processor.rfc3261.SipMessageManager;
import com.jive.sip.processor.rfc3261.parsing.RfcMessageParserBuilder;
import com.jive.sip.processor.rfc3261.parsing.RfcSipMessageParser;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpMessageDecoder extends ByteToMessageDecoder
{

  private static enum State
  {
    ReadingHeaders,
    ReadingBody
  }

  private final SipMessageManager manager;
  private final RfcSipMessageParser parser = new RfcMessageParserBuilder().build();
  private State state = State.ReadingHeaders;
  private RawMessage msg;

  private static byte[] SEPERATOR = new byte[]
  { 13, 10, 13, 10 };

  public TcpMessageDecoder(final SipMessageManager manager)
  {
    this.manager = manager;
  }


  @Override
  protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception
  {

    // look for a CRLFCRLF, which will be the end of the headers:

    final int pos = in.readerIndex();

    if (this.state == State.ReadingHeaders)
    {

      // skip leading CRLFs.
      while ((in.readableBytes() >= 2) && (in.getByte(in.readerIndex()) == 13) && (in.getByte(in.readerIndex() + 1) == 10))
      {
        in.skipBytes(2);
      }

      for (int i = in.readerIndex(); i < in.readableBytes(); ++i)
      {


        if (!findCrlf(in, i))
        {
          continue;
        }

        final int len = (i - in.readerIndex()) + 4;


        if (i == 0)
        {
          in.skipBytes(len);
          continue;
        }

        final ByteBuffer buf = in.nioBuffer(in.readerIndex(), len);

        in.skipBytes(len);

        try
        {
          this.msg = this.parser.parse(buf);
        }
        catch (final Exception e)
        {
          log.warn("Failed to parse message", e);
          continue;
        }

        final Optional<Integer> contentLength = this.msg.getContentLength();

        // log.debug("Found CRLF [CL={}] at {}: {}", contentLength.or(0), i, this.msg.toString());

        if (contentLength.isPresent())
        {
          this.state = State.ReadingBody;
        }
        else
        {
          processMessage(null, out);
        }

      }

    }

    if (this.state == State.ReadingBody)
    {

      final int len = this.msg.getContentLength().get();

      if (in.readableBytes() < len)
      {
        return;
      }

      final ByteBuffer buf = in.nioBuffer(in.readerIndex(), len);

      in.skipBytes(len);

      processMessage(buf, out);

    }


  }


  private void processMessage(final ByteBuffer body, final List<Object> out)
  {

    if (body != null)
    {
      final byte[] data = new byte[body.remaining()];
      body.get(data);
      this.msg.setBody(data);
    }

    final SipMessage msg = this.manager.convert(this.msg);

    this.msg = null;
    this.state = State.ReadingHeaders;

    out.add(msg);

  }


  private boolean findCrlf(final ByteBuf in, final int start)
  {

    for (int i = 0; i < SEPERATOR.length; ++i)
    {

      if (in.getByte(start + i) != SEPERATOR[i])
      {
        return false;
      }

    }

    return true;

  }

}
