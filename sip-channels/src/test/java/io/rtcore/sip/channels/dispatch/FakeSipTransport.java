package io.rtcore.sip.channels.dispatch;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import io.rtcore.sip.message.message.SipRequest;

public class FakeSipTransport implements Subscriber<SipRequest> {

  private Subscription subscription;

  @Override
  public void onSubscribe(final Subscription subscription) {
    this.subscription = subscription;
    this.subscription.request(1);
  }

  @Override
  public void onNext(final SipRequest req) {
    System.err.println(req);
    this.subscription.request(1);
  }

  @Override
  public void onError(final Throwable throwable) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: FakeSipTransport.onError invoked.");
  }

  @Override
  public void onComplete() {
  }

}
