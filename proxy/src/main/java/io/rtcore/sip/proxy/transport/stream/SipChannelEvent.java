package io.rtcore.sip.proxy.transport.stream;

import org.immutables.value.Value;

import io.netty.channel.Channel;
import io.netty.handler.ssl.SslCompletionEvent;

public abstract class SipChannelEvent {

  private final Channel ch;

  private SipChannelEvent(Channel ch) {
    this.ch = ch;
  }

  public static class TlsStreamCompleted {

    public TlsStreamCompleted(Channel ch, SslCompletionEvent evt) {
      // TODO Auto-generated constructor stub
    }

  }

  @Value.Immutable
  public interface ChannelConnected {

    @Value.Parameter
    Channel ch();

    @Value.Parameter
    ImmutableTlsInfo tlsInfo();

  }

  public static class ChannelFailure extends SipChannelEvent {

    public ChannelFailure(Channel ch, Throwable evt) {
      super(ch);
    }

  }

}
