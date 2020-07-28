package io.rtcore.sip.netty.codec;


import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;

public class DefaultSipContent extends DefaultSipObject implements SipContent {

  private final ByteBuf content;

  /**
   * Creates a new instance with the specified chunk content.
   */
  public DefaultSipContent(ByteBuf content) {
    if (content == null) {
      throw new NullPointerException("content");
    }
    this.content = content;
  }

  @Override
  public ByteBuf content() {
    return content;
  }

  @Override
  public SipContent copy() {
    return replace(content.copy());
  }

  @Override
  public SipContent duplicate() {
    return replace(content.duplicate());
  }

  @Override
  public SipContent retainedDuplicate() {
    return replace(content.retainedDuplicate());
  }

  @Override
  public SipContent replace(ByteBuf content) {
    return new DefaultSipContent(content);
  }

  @Override
  public int refCnt() {
    return content.refCnt();
  }

  @Override
  public SipContent retain() {
    content.retain();
    return this;
  }

  @Override
  public SipContent retain(int increment) {
    content.retain(increment);
    return this;
  }

  @Override
  public SipContent touch() {
    content.touch();
    return this;
  }

  @Override
  public SipContent touch(Object hint) {
    content.touch(hint);
    return this;
  }

  @Override
  public boolean release() {
    return content.release();
  }

  @Override
  public boolean release(int decrement) {
    return content.release(decrement);
  }

  @Override
  public String toString() {
    return StringUtil.simpleClassName(this)
      +
      "(data: "
      + content()
      + ", decoderResult: "
      + decoderResult()
      + ')';
  }

}
