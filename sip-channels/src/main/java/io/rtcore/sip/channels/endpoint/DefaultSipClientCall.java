package io.rtcore.sip.channels.endpoint;

import java.util.concurrent.Flow.Subscriber;

import io.rtcore.sip.channels.SipClientCall;
import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.api.Reason;

public class DefaultSipClientCall implements SipClientCall {

  @Override
  public void subscribe(final Subscriber<? super SipResponse> subscriber) {

  }

  @Override
  public SipClientCall cancel(final Iterable<Reason> reason) {
    return this;
  }

}
