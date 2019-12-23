package io.rtcore.sip.signaling.call.negotiation;

import java.util.Optional;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface IceNegotiationSession extends NegotiationSession {

  Optional<Boolean> canTrickleIceCandidates();

  Completable addIceCandidate(String candidate);

  Observable<String> iceCandidate();

  Observable<String> iceCandidateError();

}
