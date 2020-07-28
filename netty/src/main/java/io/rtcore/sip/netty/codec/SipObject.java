package io.rtcore.sip.netty.codec;


import io.netty.handler.codec.DecoderResult;

public interface SipObject {

  DecoderResult decoderResult();

  void setDecoderResult(DecoderResult failure);

}
