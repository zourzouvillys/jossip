package io.rtcore.sip.channels.netty.codec;

import io.netty.channel.CombinedChannelDuplexHandler;

public class SipCodec extends CombinedChannelDuplexHandler<SipObjectDecoder, SipObjectEncoder> {

  public SipCodec() {
    super(
      new SipObjectDecoder(),
      new SipObjectEncoder());
  }

}
