package io.rtcore.sip.channels.netty;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelOption;
import io.rtcore.sip.channels.internal.AbstractManagedSipChannelBuilder;
import io.rtcore.sip.channels.internal.ManagedSipChannel;
import io.rtcore.sip.channels.internal.SipChannelCredentials;

public class NettySipChannelBuilder extends AbstractManagedSipChannelBuilder<NettySipChannelBuilder> {

  private final String target;
  private final Map<ChannelOption<?>, Object> channelOptions = new HashMap<>();

  public NettySipChannelBuilder(final String target, final SipChannelCredentials creds, final SipProtocolNegotiator.ClientFactory negotiator) {
    this.target = checkNotNull(target, "target");
  }

  /**
   * Specifies a channel option. As the underlying channel as well as network implementation may
   * ignore this value applications should consider it a hint.
   */

  public <T> NettySipChannelBuilder withOption(final ChannelOption<T> option, final T value) {
    this.channelOptions.put(option, value);
    return this;
  }

  /**
   *
   */

  @Override
  public ManagedSipChannel build() {
    throw new IllegalArgumentException();
  }

  /**
   *
   */

  public static NettySipChannelBuilder forTarget(final String target, final SipChannelCredentials creds) {
    return new NettySipChannelBuilder(target, creds, SipProtocolNegotiators.forClient(creds));
  }

}
