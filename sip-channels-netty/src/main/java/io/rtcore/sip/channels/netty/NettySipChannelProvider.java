package io.rtcore.sip.channels.netty;

import com.google.auto.service.AutoService;

import io.rtcore.sip.channels.ManagedSipChannelBuilder;
import io.rtcore.sip.channels.ManagedSipChannelProvider;
import io.rtcore.sip.channels.SipChannelCredentials;

@AutoService(ManagedSipChannelProvider.class)
public final class NettySipChannelProvider implements ManagedSipChannelProvider {

  @Override
  public int priority() {
    return 5;
  }

  @Override
  public ManagedSipChannelBuilder<?> newChannelBuilder(final String target, final SipChannelCredentials creds) {
    return NettySipChannelBuilder.forTarget(target, creds);
  }

}