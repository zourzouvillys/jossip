package io.rtcore.sip.channels.interceptors;

import io.rtcore.sip.channels.api.SipServerExchange;
import io.rtcore.sip.channels.api.SipServerExchange.Listener;

public class ForwardingSipServerExchangeListener implements SipServerExchange.Listener {

  private Listener delegate;

  public ForwardingSipServerExchangeListener(SipServerExchange.Listener delegate) {
    this.delegate = delegate;
  }

  @Override
  public void onCancel() {
    this.delegate.onCancel();
  }

}
