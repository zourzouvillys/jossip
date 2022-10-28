package io.rtcore.sip.channels.interceptors;

import java.util.List;

import com.google.common.base.Preconditions;

import io.rtcore.sip.channels.api.SipChannel;

public class SipClientInterceptors {

  public static SipChannel intercept(SipChannel channel, Iterable<? extends SipClientInterceptor> interceptors) {
    Preconditions.checkNotNull(channel, "channel");
    for (SipClientInterceptor interceptor : interceptors) {
      channel = new InterceptorSipChannel(channel, interceptor);
    }
    return channel;
  }

  public static SipChannel intercept(SipChannel channel, SipClientInterceptor... interceptors) {
    return intercept(channel, List.of(interceptors));
  }

}
