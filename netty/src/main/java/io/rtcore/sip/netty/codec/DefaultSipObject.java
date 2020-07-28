package io.rtcore.sip.netty.codec;


import io.netty.handler.codec.DecoderResult;

public class DefaultSipObject implements SipObject {

  private DecoderResult decoderResult;

  public DecoderResult decoderResult() {
    return this.decoderResult;
  }

  @Override
  public void setDecoderResult(DecoderResult failure) {
    this.decoderResult = failure;
  }

}
