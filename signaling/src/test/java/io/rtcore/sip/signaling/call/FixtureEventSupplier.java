package io.rtcore.sip.signaling.call;

import java.util.List;

import io.rtcore.sip.signaling.call.OfferAnswerCategorization.Event;

public interface FixtureEventSupplier {

  List<Event> fetch();

}
