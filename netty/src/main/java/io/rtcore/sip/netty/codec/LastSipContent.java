package io.rtcore.sip.netty.codec;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;

public interface LastSipContent extends SipContent {

  /**
   * The 'end of content' marker.
   */
  
  LastSipContent EMPTY_LAST_CONTENT = new LastSipContent() {

    @Override
    public ByteBuf content() {
      return Unpooled.EMPTY_BUFFER;
    }

    @Override
    public LastSipContent copy() {
      return EMPTY_LAST_CONTENT;
    }

    @Override
    public LastSipContent duplicate() {
      return this;
    }

    @Override
    public LastSipContent replace(ByteBuf content) {
      return new DefaultLastSipContent(content);
    }

    @Override
    public LastSipContent retainedDuplicate() {
      return this;
    }

    @Override
    public SipHeaders trailingHeaders() {
      return EmptySipHeaders.INSTANCE;
    }

    @Override
    public DecoderResult decoderResult() {
      return DecoderResult.SUCCESS;
    }

    @Override
    public void setDecoderResult(DecoderResult result) {
      throw new UnsupportedOperationException("read only");
    }

    @Override
    public int refCnt() {
      return 1;
    }

    @Override
    public LastSipContent retain() {
      return this;
    }

    @Override
    public LastSipContent retain(int increment) {
      return this;
    }

    @Override
    public LastSipContent touch() {
      return this;
    }

    @Override
    public LastSipContent touch(Object hint) {
      return this;
    }

    @Override
    public boolean release() {
      return false;
    }

    @Override
    public boolean release(int decrement) {
      return false;
    }

    @Override
    public String toString() {
      return "EmptyLastSipContent";
    }
    
  };

  SipHeaders trailingHeaders();

  @Override
  LastSipContent copy();

  @Override
  LastSipContent duplicate();

  @Override
  LastSipContent retainedDuplicate();

  @Override
  LastSipContent replace(ByteBuf content);

  @Override
  LastSipContent retain(int increment);

  @Override
  LastSipContent retain();

  @Override
  LastSipContent touch();

  @Override
  LastSipContent touch(Object hint);

}
