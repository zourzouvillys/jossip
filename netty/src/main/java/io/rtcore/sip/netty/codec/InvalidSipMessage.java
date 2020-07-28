package io.rtcore.sip.netty.codec;


import io.netty.handler.codec.DecoderResult;

public class InvalidSipMessage implements SipMessage {

  private DecoderResult failure;

  @Override
  public SipHeaders headers() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipMessage.headers invoked.");
  }

  @Override
  public void setDecoderResult(DecoderResult failure) {
    this.failure = failure;
  }

  @Override
  public SipVersion protocolVersion() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipMessage.protocolVersion invoked.");
  }

  @Override
  public DecoderResult decoderResult() {
    return this.failure;
  }

}
