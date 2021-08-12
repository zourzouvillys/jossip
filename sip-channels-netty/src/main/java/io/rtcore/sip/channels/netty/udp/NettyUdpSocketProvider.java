package io.rtcore.sip.channels.netty.udp;

import com.google.auto.service.AutoService;

import io.rtcore.sip.channels.ManagedSipUdpSocketBuilder;
import io.rtcore.sip.channels.ManagedSipUdpSocketProvider;

@AutoService(ManagedSipUdpSocketProvider.class)
public class NettyUdpSocketProvider implements ManagedSipUdpSocketProvider {

  @Override
  public ManagedSipUdpSocketBuilder<?> builder() {
    return new NettyUdpServerBuilder();
  }

}
