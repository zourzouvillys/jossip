package io.rtcore.sip.channels.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.AppendableCharSequence;

public class SipHeaderParser implements ByteProcessor {

  private final AppendableCharSequence seq;
  private final int maxLength;
  int size;

  SipHeaderParser(AppendableCharSequence seq, int maxLength) {
    this.seq = seq;
    this.maxLength = maxLength;
  }

  public AppendableCharSequence parse(ByteBuf buffer) {
    final int oldSize = size;
    seq.reset();
    int i = buffer.forEachByte(this);
    if (i == -1) {
      size = oldSize;
      return null;
    }
    buffer.readerIndex(i + 1);
    return seq;
  }

  public void reset() {
    size = 0;
  }

  @Override
  public boolean process(byte value) throws Exception {

    char nextByte = (char) (value & 0xFF);

    if (nextByte == '\n') {

      int len = seq.length();

      // Drop CR if we had a CRLF pair
      if (len >= 1 && seq.charAtUnsafe(len - 1) == '\r') {
        --size;
        seq.setLength(len - 1);
      }

      return false;

    }

    increaseCount();

    seq.append(nextByte);
    return true;
  }

  protected final void increaseCount() {
    if (++size > maxLength) {
      // TODO: Respond with Bad Request and discard the traffic
      // or close the connection?
      // No need to notify the upstream handlers - just log.
      // If decoding a response, just throw an exception.
      throw newException(maxLength);
    }
  }

  protected TooLongFrameException newException(int maxLength) {
    return new TooLongFrameException("sip header is larger than " + maxLength + " bytes: " + seq.toString());
  }

}
