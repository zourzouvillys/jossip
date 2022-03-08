package io.rtcore.sip.channels.api;

import java.util.concurrent.CompletionStage;

import io.rtcore.sip.channels.internal.SipAttributes;

public abstract class ForwardingSipServerExchange implements SipServerExchange {

  private final SipServerExchange delegate;

  public ForwardingSipServerExchange(SipServerExchange delegate) {
    this.delegate = delegate;
  }

  @Override
  public SipRequestFrame request() {
    return this.delegate.request();
  }

  @Override
  public SipAttributes attributes() {
    return this.delegate.attributes();
  }

  @Override
  public CompletionStage<?> onNext(SipResponseFrame response) {
    return this.delegate.onNext(response);
  }

  @Override
  public void onError(Throwable error) {
    this.delegate.onError(error);
  }

  @Override
  public void onComplete() {
    this.delegate.onComplete();
  }

  @Override
  public boolean isCancelled() {
    return this.delegate.isCancelled();
  }

}
