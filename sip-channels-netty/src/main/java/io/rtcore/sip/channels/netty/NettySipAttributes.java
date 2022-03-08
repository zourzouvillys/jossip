package io.rtcore.sip.channels.netty;

import io.netty.channel.Channel;
import io.rtcore.sip.channels.internal.SipAttributes;

public class NettySipAttributes {

  /**
   * the identifier for this specific flow.
   */

  public static final SipAttributes.Key<Channel> ATTR_CHANNEL = SipAttributes.Key.create("channel");

}
