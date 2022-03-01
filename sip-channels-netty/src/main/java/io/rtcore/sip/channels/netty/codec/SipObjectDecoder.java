package io.rtcore.sip.channels.netty.codec;

import java.util.List;

import com.google.common.primitives.UnsignedInteger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.search.AbstractMultiSearchProcessorFactory;
import io.netty.buffer.search.MultiSearchProcessor;
import io.netty.buffer.search.MultiSearchProcessorFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.rtcore.sip.message.message.SipMessage;
import io.rtcore.sip.message.message.api.headers.MIMEType;
import io.rtcore.sip.message.processor.rfc3261.DefaultSipMessage;
import io.rtcore.sip.message.processor.rfc3261.RfcSipMessageManager;

class SipObjectDecoder extends ByteToMessageDecoder {

  private final int maxMessageSize;

  private static final byte[][] needles = { "\n\n".getBytes(), "\r\n\r\n".getBytes() };

  private static final MultiSearchProcessorFactory newlineSearcher =
    AbstractMultiSearchProcessorFactory.newAhoCorasicSearchProcessorFactory(needles);

  // private AppendableCharSequence seq;

  public SipObjectDecoder() {
    this(8192, 1024);
  }

  public SipObjectDecoder(int maxMessageSize) {
    this(maxMessageSize, 1024);
  }

  public SipObjectDecoder(final int maxMessageSize, final int initialBufferSize) {
    // super.setSingleDecode(false);
    // super.setDiscardAfterReads(4);
    this.maxMessageSize = maxMessageSize;
    super.setSingleDecode(true);
  }

  @Override
  protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {

    in.markReaderIndex();

    final MultiSearchProcessor processor = newlineSearcher.newSearchProcessor();

    final int idx = in.forEachByte(processor);

    if (idx == -1) {

      // no occurences found.
      if (in.readableBytes() > this.maxMessageSize) {
        throw new CorruptedFrameException("message too large");
      }
      // need more data.

      in.resetReaderIndex();
      return;

    }

    final int sidx = (idx - needles[processor.getFoundNeedleId()].length) + 1;

    if (sidx == in.readerIndex()) {
      // add a CRLF notification.
      out.add(in.readRetainedSlice((idx + 1) - in.readerIndex()));
      return;
    }

    // read headers
    final SipMessage message = this.decodeHeaders(in.readSlice((idx - in.readerIndex()) + 1));

    final int contentLength =
      message
        .getHeader(DefaultSipMessage.CONTENT_LENGTH)
        .orElse(UnsignedInteger.ZERO)
        .intValue();

    if (contentLength == 0) {
      out.add(message);
      return;
    }

    if (in.isReadable(contentLength)) {
      // assume application SDP if nothing else provided, some broken devices don't send it.
      out.add(message.withBody(message.contentType().orElse(MIMEType.APPLICATION_SDP), ByteBufUtil.getBytes(in.readSlice(contentLength))));
      return;
    }

    // try again when we have more data. note that this sucks ...
    in.resetReaderIndex();

  }

  private SipMessage decodeHeaders(final ByteBuf headers) {
    return RfcSipMessageManager.defaultInstance().parseMessage(headers.nioBuffer());
  }

}
