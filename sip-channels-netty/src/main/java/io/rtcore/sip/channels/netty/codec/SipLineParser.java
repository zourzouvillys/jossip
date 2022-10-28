package io.rtcore.sip.channels.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.AppendableCharSequence;

final class SipLineParser extends SipHeaderParser {

  SipLineParser(AppendableCharSequence seq, int maxLength) {
    super(seq, maxLength);
  }

  @Override
  public AppendableCharSequence parse(ByteBuf buffer) {
    reset();
    return super.parse(buffer);
  }

  @Override
  public boolean process(byte value) throws Exception {
    // we could read/skip CRLFs here?
    return super.process(value);
  }

  @Override
  protected TooLongFrameException newException(int maxLength) {
    return new TooLongFrameException("initial SIP line is larger than " + maxLength + " bytes.");
  }
}
