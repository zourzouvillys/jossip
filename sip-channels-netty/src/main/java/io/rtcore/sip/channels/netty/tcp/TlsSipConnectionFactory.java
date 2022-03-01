package io.rtcore.sip.channels.netty.tcp;

import io.netty.channel.Channel;

@FunctionalInterface
public interface TlsSipConnectionFactory {

  TlsSipConnection createConnection(Channel ch);

}
