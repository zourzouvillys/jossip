package io.rtcore.sip.channels.api;

import java.util.Set;
import java.util.function.Predicate;

import io.rtcore.sip.common.iana.SipMethodId;

public class SipFramePredicates {

  public static Predicate<SipRequestFrame> isMethod(SipMethodId... methods) {
    return isMethod(Set.of(methods));
  }

  public static Predicate<SipRequestFrame> isMethod(Set<SipMethodId> methods) {
    return req -> methods.contains(req.initialLine().method());
  }

}
