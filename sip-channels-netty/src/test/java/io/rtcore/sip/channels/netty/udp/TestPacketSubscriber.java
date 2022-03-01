package io.rtcore.sip.channels.netty.udp;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;

import io.rtcore.sip.channels.SipWirePacket;
import io.rtcore.sip.channels.SipWireProducer;
import io.rtcore.sip.message.message.SipMessage;

public class TestPacketSubscriber implements Flow.Subscriber<SipWireProducer> {

  private final LinkedList<SipMessage> packets = new LinkedList<>();
  private Subscription subscription;

  @Override
  public void onSubscribe(final Subscription subscription) {
    this.subscription = subscription;
    this.subscription.request(1);
  }

  public List<SipMessage> packets() {
    return this.packets;
  }

  @Override
  public void onNext(final SipWireProducer item) {
    try (SipWirePacket in = item.next()) {
      this.packets.add(in.payload());
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
    finally {
      this.subscription.request(1);
    }
  }

  @Override
  public void onError(final Throwable throwable) {
  }

  @Override
  public void onComplete() {
  }

}
