package io.rtcore.sip.channels.netty.tcp;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public interface TcpConnectionConfig {

  default int recvBufferSize() {
    return 8192;
  }

  default int sendBufferSize() {
    return 8192;
  }

}
