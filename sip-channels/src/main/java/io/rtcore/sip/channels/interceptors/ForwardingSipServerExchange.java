package io.rtcore.sip.channels.interceptors;

import java.util.concurrent.CompletionStage;

import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipServerExchange;

public abstract class ForwardingSipServerExchange<ReqT, ResT> implements SipServerExchange<ReqT, ResT> {

  private final SipServerExchange<ReqT, ResT> delegate;

  public ForwardingSipServerExchange(SipServerExchange<ReqT, ResT> delegate) {
    this.delegate = delegate;
  }

  @Override
  public ReqT request() {
    return this.delegate.request();
  }

  @Override
  public SipAttributes attributes() {
    return this.delegate.attributes();
  }

  @Override
  public CompletionStage<?> onNext(ResT response) {
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
