package io.rtcore.sip.channels.netty.codec;

import io.netty.channel.CombinedChannelDuplexHandler;

public class SipCodec extends CombinedChannelDuplexHandler<SipStreamDecoder, SipObjectEncoder> {

  public SipCodec() {
    super(
      new SipStreamDecoder(),
      new SipObjectEncoder());
  }

  public SipCodec(int maxMessageSize) {
    super(
      new SipStreamDecoder(maxMessageSize),
      new SipObjectEncoder());
  }

  public SipCodec(int maxMessageSize, int initialBufferSize) {
    super(
      new SipStreamDecoder(maxMessageSize, initialBufferSize),
      new SipObjectEncoder());
  }

}
