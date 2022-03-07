package io.rtcore.sip.channels.netty.udp;

import com.google.auto.service.AutoService;

import io.rtcore.sip.channels.internal.ManagedSipUdpSocketBuilder;
import io.rtcore.sip.channels.internal.ManagedSipUdpSocketProvider;

@AutoService(ManagedSipUdpSocketProvider.class)
public class NettyUdpSocketProvider implements ManagedSipUdpSocketProvider {

  @Override
  public ManagedSipUdpSocketBuilder<?> builder() {
    return new NettyUdpServerBuilder();
  }

}
