package io.rtcore.sip.netty.codec;

import io.netty.channel.CombinedChannelDuplexHandler;

public class SipStreamCodec extends CombinedChannelDuplexHandler<SipStreamDecoder, SipObjectEncoder> {

  public SipStreamCodec() {
    super(
      new SipStreamDecoder(),
      new SipObjectEncoder());
  }

  public SipStreamCodec(int maxMessageSize) {
    super(
      new SipStreamDecoder(maxMessageSize),
      new SipObjectEncoder());
  }

  public SipStreamCodec(int maxMessageSize, int initialBufferSize) {
    super(
      new SipStreamDecoder(maxMessageSize, initialBufferSize),
      new SipObjectEncoder());
  }

}
