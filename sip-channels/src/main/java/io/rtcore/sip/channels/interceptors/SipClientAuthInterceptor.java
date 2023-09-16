package io.rtcore.sip.channels.interceptors;

import java.util.Optional;

import io.rtcore.sip.channels.api.SipCallOptions;
import io.rtcore.sip.channels.api.SipChannel;
import io.rtcore.sip.channels.api.SipClientExchange;
import io.rtcore.sip.frame.SipRequestFrame;

public class SipClientAuthInterceptor implements SipClientInterceptor {

  private final String username;
  private final String password;

  public SipClientAuthInterceptor(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public SipClientExchange interceptCall(SipRequestFrame request, SipCallOptions options, SipChannel next) {
    return new MultiSipClientExchange(next.exchange(request), new AuthGenerator(request, options, next, this.username, this.password));
  }

  record Attempt(SipRequestFrame request, SipCallOptions options, SipChannel next) {
  }

  @FunctionalInterface
  interface Generator {
    Optional<Attempt> next(Throwable t);
  }

}
