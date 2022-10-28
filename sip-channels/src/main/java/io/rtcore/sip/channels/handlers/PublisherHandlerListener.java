package io.rtcore.sip.channels.handlers;

import org.reactivestreams.Publisher;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.api.SipResponseFrame;
import io.rtcore.sip.channels.api.SipServerExchange;
import io.rtcore.sip.channels.api.SipServerExchange.Listener;

public class PublisherHandlerListener implements Listener {

  private final SipServerExchange<SipRequestFrame, SipResponseFrame> exchange;
  private final Disposable handle;

  public PublisherHandlerListener(SipServerExchange<SipRequestFrame, SipResponseFrame> exchange, Publisher<SipResponseFrame> response) {
    this.exchange = exchange;
    this.handle = Flowable.fromPublisher(response).subscribe(this.exchange::onNext, this.exchange::onError, this.exchange::onComplete);
  }

  @Override
  public void onCancel() {
    this.handle.dispose();
  }

}
