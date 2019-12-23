package io.rtcore.sip.signaling.call.negotiation;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface NegotiationService {

  interface Session {

    String name();

    String sessionDescription();

    boolean negotiationNeeded();

  }

  /**
   * 
   */

  Flowable<Session> createSession(SessionDescription remoteOffer, boolean forkable);

  /**
   * 
   */

  Single<Session> createOffer(String sessionId);

  /**
   * 
   */

  Single<Session> createAnswer(String sessionId, String remoteOffer);

  /**
   * 
   */

  Single<Session> setRemoteDescription(SessionDescription remote);

  /**
   * trickle candidates (in both directions).
   * 
   * @param sessionId
   * @param candidates
   */

  Flowable<String> trickeIce(String sessionId, Flowable<String> candidates);

}
