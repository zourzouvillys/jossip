package io.rtcore.sip.netty.codec;


import io.netty.buffer.ByteBuf;

public class DefaultFullSipResponse extends DefaultFullSipMessage implements FullSipResponse {

  public DefaultFullSipResponse(SipVersion protocolVersion, SipResponseStatus status, ByteBuf content, SipHeaders copy, SipHeaders copy2) {
    // TODO Auto-generated constructor stub
  }

  @Override
  public SipResponseStatus status() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipResponse.status invoked.");
  }

  @Override
  public FullSipResponse copy() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipResponse.copy invoked.");
  }

  @Override
  public FullSipResponse duplicate() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipResponse.duplicate invoked.");
  }

  @Override
  public FullSipResponse retainedDuplicate() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipResponse.retainedDuplicate invoked.");
  }

  @Override
  public FullSipResponse replace(ByteBuf content) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipResponse.replace invoked.");
  }

  @Override
  public FullSipResponse retain(int increment) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipResponse.retain invoked.");
  }

  @Override
  public FullSipResponse retain() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipResponse.retain invoked.");
  }

  @Override
  public FullSipResponse touch() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipResponse.touch invoked.");
  }

  @Override
  public FullSipResponse touch(Object hint) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipResponse.touch invoked.");
  }

}
