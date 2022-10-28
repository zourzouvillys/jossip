package io.rtcore.sip.channels.exchange;

import java.util.function.Supplier;

import io.reactivex.rxjava3.core.Flowable;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipChannel;
import io.rtcore.sip.channels.api.SipClientExchange;
import io.rtcore.sip.channels.api.SipRequestFrame;

/**
 * implementation of a SIP exchange which can attempt different targets in a serial pattern. It does
 * not provide forking.
 */

public class SipMultiExchange implements SipClientExchange {

  private final SipRequestFrame originalRequest;
  private final Supplier<SipChannel> next;

  private SipMultiExchange(SipRequestFrame req, Supplier<SipChannel> next) {
    this.originalRequest = req;
    this.next = next;
  }

  @Override
  public SipRequestFrame request() {
    return this.originalRequest;
  }

  @Override
  public SipAttributes attributes() {
    return SipAttributes.of();
  }

  /**
   * each response received from a downstream. the events will represent the standard SIP behavior:
   * 1xx+, followed by a final response. multiple 2xx responses may be provided in the case of an
   * INVITE.
   * 
   * the returned Flowable will complete once no further messages may be received.
   * 
   */

  @Override
  public Flowable<Event> responses() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipClientExchange.responses invoked.");
  }

  /**
   * attempt to abort the exchange. if there has not yet been a request transmitted, then this will
   * return true, otherwise false.
   */

  @Override
  public boolean cancel() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipClientExchange.cancel invoked.");
  }

  /**
   * 
   */

  public static SipMultiExchange exchange(SipRequestFrame req, Supplier<SipChannel> next) {
    return new SipMultiExchange(req, next);
  }

}
