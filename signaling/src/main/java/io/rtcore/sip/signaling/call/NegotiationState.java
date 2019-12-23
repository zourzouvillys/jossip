package io.rtcore.sip.signaling.call;

public enum NegotiationState {

  NONE,

  HAVE_LOCAL_OFFER,
  HAVE_REMOTE_OFFER,

  HAVE_LOCAL_PRANSWER,
  HAVE_REMOTE_PRANSWER,

  STABLE,

}
