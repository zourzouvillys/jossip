package io.rtcore.sip.signaling.call.negotiation;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(overshadowImplementation = true)
public interface SessionDescription {

  @Value.Parameter
  SdpType type();

  @Value.Parameter
  String sdp();

  static SessionDescription of(SdpType type, String sdp) {
    return ImmutableSessionDescription.of(type, sdp);
  }

  static SessionDescription offer(String sdp) {
    return of(SdpType.OFFER, sdp);
  }

  static SessionDescription answer(String sdp) {
    return of(SdpType.ANSWER, sdp);
  }

  static SessionDescription provisionalAnswer(String sdp) {
    return of(SdpType.PRANSWER, sdp);
  }

  static SessionDescription rollback() {
    return of(SdpType.ROLLBACK, "");
  }

}
