package io.rtcore.sip.signaling.call.adapter;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import io.rtcore.sip.iana.SipOptionTags;

public class CallService {

  private static final ImmutableSet<SipOptionTags> supportedTags =
    ImmutableSet.of(SipOptionTags.$199,
      SipOptionTags.$100REL,
      SipOptionTags.FROM_CHANGE,
      SipOptionTags.ICE,
      SipOptionTags.JOIN,
      SipOptionTags.REPLACES,
      SipOptionTags.NOREFERSUB,
      SipOptionTags.NOSUB,
      SipOptionTags.ANSWERMODE,
      SipOptionTags.PRIVACY,
      SipOptionTags.TRICKLE_ICE,
      SipOptionTags.GRUU,
      SipOptionTags.TIMER,
      SipOptionTags.TDIALOG,
      SipOptionTags.HISTINFO,
      SipOptionTags.RESOURCE_PRIORITY,
      SipOptionTags.SIPREC,
      SipOptionTags.UUI);

  public CallService() {

  }

  public Set<SipOptionTags> supportedOptions() {
    return supportedTags;
  }

}
