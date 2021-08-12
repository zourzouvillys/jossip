package io.rtcore.sip.channels.netty;


import java.net.SocketAddress;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import io.rtcore.sip.channels.SipClientTransport;
import io.rtcore.sip.channels.SipClientTransportOptions;
import io.rtcore.sip.message.message.SipMessage;

public class NettySipClientTransport implements SipClientTransport, Flow.Publisher<SipMessage> {

  private final SocketAddress serverAddress;
  private final SipClientTransportOptions options;

  public NettySipClientTransport(final SocketAddress serverAddress, final SipClientTransportOptions options) {
    this.serverAddress = serverAddress;
    this.options = options;
  }

  /**
   * subscribe to the specified publisher to provide messages to write over this transport.
   *
   * will not read until the transport is connected.
   *
   */

  public void write(final Flow.Publisher<SipMessage> writePublisher) {
    //
  }

  /**
   * triggers the connection and reading.
   */

  @Override
  public void subscribe(final Subscriber<? super SipMessage> readSubsriber) {
    readSubsriber.onSubscribe(new Connector(readSubsriber));
  }

  static class Connector implements Subscription {

    private final Subscriber<? super SipMessage> subscriber;

    public Connector(final Subscriber<? super SipMessage> subscriber) {
      this.subscriber = subscriber;
    }

    @Override
    public void request(final long n) {
      //
    }

    @Override
    public void cancel() {
      // terminate the connectino is possible.
    }

  }

}
