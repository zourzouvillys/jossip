package io.rtcore.sip.signaling.call;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.signaling.call.OfferAnswerContext;
import io.rtcore.sip.signaling.call.OfferValidity;

class OfferValidityTest {

  @Test
  void test() {
    OfferValidity
      .offerType(SipMethod.INVITE)
      .filter(t -> t.contexts().contains(OfferAnswerContext.Initial))
      .map(t -> t.answers())
      .get();
  }

}
