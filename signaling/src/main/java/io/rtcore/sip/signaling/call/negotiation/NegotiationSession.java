package io.rtcore.sip.signaling.call.negotiation;

import java.util.Optional;

import javax.annotation.Nonnull;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

/**
 * interface used to map SIP negotiation to another service, very similar to RTCPeerConnection but
 * without any ICE/transport connection management stuff. high level ICE specific stuff is in
 * {@link IceNegotiationSession}. note that both these interfaces only provide what is needed for
 * SDP negotiation, nothing else.
 */

public interface NegotiationSession {

  Single<SessionDescription> createOffer();

  Single<SessionDescription> createAnswer();

  SignalingState signalingState();

  Completable setLocalDescription(Optional<SessionDescription> sdp);

  default Completable setLocalDescription(@Nonnull SessionDescription sdp) {
    return setLocalDescription(Optional.of(sdp));
  }

  default Completable setLocalDescription() {
    return setLocalDescription(Optional.empty());
  }

  Completable setRemoteDescription(SessionDescription sdp);

  /**
   * integer increments each time a new negotiation is needed.
   */

  Observable<Integer> negotiationNeeded();

  /// accessors

  default Optional<SessionDescription> localDescription() {
    return pendingLocalDescription().or(this::currentLocalDescription);
  }

  default Optional<SessionDescription> remoteDescription() {
    return pendingRemoteDescription().or(this::currentRemoteDescription);
  }

  Optional<SessionDescription> currentLocalDescription();

  Optional<SessionDescription> pendingLocalDescription();

  Optional<SessionDescription> currentRemoteDescription();

  Optional<SessionDescription> pendingRemoteDescription();

  //

  Completable close();

}
