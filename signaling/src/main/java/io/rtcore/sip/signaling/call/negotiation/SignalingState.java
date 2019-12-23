package io.rtcore.sip.signaling.call.negotiation;

public enum SignalingState {

  STABLE,

  HAVE_LOCAL_OFFER,
  HAVE_REMOTE_OFFER,

  HAVE_LOCAL_PRANSWER,
  HAVE_REMOTE_PRANSWER,

  CLOSED,

}
