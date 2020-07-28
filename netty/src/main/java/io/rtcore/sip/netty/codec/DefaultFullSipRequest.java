package io.rtcore.sip.netty.codec;


import io.netty.buffer.ByteBuf;

public class DefaultFullSipRequest extends DefaultFullSipMessage implements FullSipRequest {

  public DefaultFullSipRequest(SipVersion protocolVersion, SipMethod method, String uri, ByteBuf content, SipHeaders copy, SipHeaders copy2) {
    // TODO Auto-generated constructor stub
  }

  @Override
  public SipMethod method() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipRequest.method invoked.");
  }

  @Override
  public String uri() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipRequest.uri invoked.");
  }

  @Override
  public FullSipRequest copy() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipRequest.copy invoked.");
  }

  @Override
  public FullSipRequest duplicate() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipRequest.duplicate invoked.");
  }

  @Override
  public FullSipRequest retainedDuplicate() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipRequest.retainedDuplicate invoked.");
  }

  @Override
  public FullSipRequest replace(ByteBuf content) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipRequest.replace invoked.");
  }

  @Override
  public FullSipRequest retain(int increment) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipRequest.retain invoked.");
  }

  @Override
  public FullSipRequest retain() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipRequest.retain invoked.");
  }

  @Override
  public FullSipRequest touch() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipRequest.touch invoked.");
  }

  @Override
  public FullSipRequest touch(Object hint) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FullSipRequest.touch invoked.");
  }

}
