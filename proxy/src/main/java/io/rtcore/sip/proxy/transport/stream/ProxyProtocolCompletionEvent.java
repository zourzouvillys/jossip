package io.rtcore.sip.proxy.transport.stream;

import java.net.InetSocketAddress;

public class ProxyProtocolCompletionEvent {

  private final InetSocketAddress remote;
  private final InetSocketAddress local;

  public ProxyProtocolCompletionEvent(InetSocketAddress remote, InetSocketAddress local) {
    this.remote = remote;
    this.local = local;
  }

  public InetSocketAddress remote() {
    return this.remote;
  }

  public InetSocketAddress local() {
    return this.local;
  }

}
