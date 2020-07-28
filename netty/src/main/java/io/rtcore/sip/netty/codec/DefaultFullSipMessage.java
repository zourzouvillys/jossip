package io.rtcore.sip.netty.codec;


import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderResult;

public class DefaultFullSipMessage implements FullSipMessage {

  @Override
  public SipHeaders headers() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipMessage.headers invoked.");
  }

  @Override
  public SipVersion protocolVersion() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipMessage.protocolVersion invoked.");
  }

  @Override
  public DecoderResult decoderResult() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipObject.decoderResult invoked.");
  }

  @Override
  public void setDecoderResult(DecoderResult failure) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipObject.setDecoderResult invoked.");
  }

  @Override
  public SipHeaders trailingHeaders() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: LastSipContent.trailingHeaders invoked.");
  }

  @Override
  public ByteBuf content() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: ByteBufHolder.content invoked.");
  }

  @Override
  public int refCnt() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: ReferenceCounted.refCnt invoked.");
  }

  @Override
  public boolean release() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: ReferenceCounted.release invoked.");
  }

  @Override
  public boolean release(int decrement) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: ReferenceCounted.release invoked.");
  }

  @Override
  public FullSipMessage copy() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipMessage.copy invoked.");
  }

  @Override
  public FullSipMessage duplicate() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipMessage.duplicate invoked.");
  }

  @Override
  public FullSipMessage retainedDuplicate() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipMessage.retainedDuplicate invoked.");
  }

  @Override
  public FullSipMessage replace(ByteBuf content) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipMessage.replace invoked.");
  }

  @Override
  public FullSipMessage retain(int increment) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipMessage.retain invoked.");
  }

  @Override
  public FullSipMessage retain() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipMessage.retain invoked.");
  }

  @Override
  public FullSipMessage touch() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipMessage.touch invoked.");
  }

  @Override
  public FullSipMessage touch(Object hint) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipMessage.touch invoked.");
  }

}
