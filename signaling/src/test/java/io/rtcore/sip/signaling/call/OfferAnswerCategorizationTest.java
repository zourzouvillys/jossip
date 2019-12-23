package io.rtcore.sip.signaling.call;

import static io.rtcore.sip.message.iana.SipOptionTags.$100REL;
import static io.rtcore.sip.message.message.api.SipMethod.BYE;
import static io.rtcore.sip.message.message.api.SipMethod.INVITE;
import static io.rtcore.sip.message.message.api.SipMethod.PRACK;
import static io.rtcore.sip.message.message.api.SipMethod.UPDATE;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.message.api.SipMethod;

class OfferAnswerCategorizationTest {

  /**
   * using the same CSeq for multiple requests should fail.
   */

  @Test
  void testDuplicatedCSeqUAC() {
    FixtureConfiguration
      .create()
      .given(b -> b.uac().invite(1).withOffer())
      .given(b -> b.uas().trying())
      .given(b -> b.uas().ok(SipMethod.INVITE))
      .given(b -> b.uac().ack())
      .when(b -> b.uac().update(1))
      .expectException();
  }

  /**
   * a PRACK with a valid CSeq sequence but invalid RSeq should be rejected.
   */

  @Test
  void testPrackForUnknownReliableSequenceFails() {
    FixtureConfiguration
      .create()
      .given(b -> b.uac().invite(1))
      .given(b -> b.uas().ringing(1).rseq(9999))
      .when(b -> b.uac().prack(2).rack(9919, 1))
      .expectException();
  }

  /**
   * a PRACK with an invalid CSeq referenced in the RSeq should be rejected.
   */

  @Test
  void testPrackForUnknownCSeqSequenceFails() {
    FixtureConfiguration
      .create()
      .given(b -> b.uac().invite(1))
      .given(b -> b.uas().ringing(1).rseq(9999))
      .when(b -> b.uac().prack(2).rack(9999, 1111))
      .expectException();
  }

  /**
   * call setup should result in pending for an ACK.
   */

  @Test
  void testPendingAck() {
    FixtureConfiguration.create()
      .given(b -> b.uac().invite())
      .when(b -> b.uas().ok(INVITE))
      .expectNoExceptions();
  }

  /**
   * test that ACK is correctly dispatched.
   */

  @Test
  void testAckHandling() {
    FixtureConfiguration.create()
      .given(b -> b.uac().invite())
      .given(b -> b.uas().sessionProgress())
      .when(b -> b.uac().ack())
      .expectNoExceptions();
  }

  @Test
  void testStandardOfferExchange() {

    FixtureConfiguration.create()
      .given(b -> b.uac().invite().withOffer())
      .given(b -> b.uas().trying())
      .given(b -> b.uas().sessionProgress())
      .given(b -> b.uas().sessionProgress().rseq(9999).withAnswer())
      .given(b -> b.uac().prack().rack(9999))
      .given(b -> b.uas().ok(SipMethod.PRACK))
      .given(b -> b.uas().ok(INVITE))
      .given(b -> b.uac().ack())
      .given(b -> b.uas().update().withOffer())
      .given(b -> b.uas().bye())
      .given(b -> b.uac().respond(UPDATE, 487))
      .when(b -> b.uac().bye())
      .expectNoExceptions();

  }

  @Test
  void testDelayedOfferInUnreliableResponseFails() {
    new FixtureConfiguration()
      .given(b -> b.uac().invite())
      .given(b -> b.uas().trying())
      .when(b -> b.uas().sessionProgress().withOffer())
      .expectException();
  }

  @Test
  void testDelayedOfferInReliableResponseWorks() {
    new FixtureConfiguration()
      .given(b -> b.uac().invite())
      .given(b -> b.uas().trying())
      .when(b -> b.uas().sessionProgress().rseq(1).withOffer())
      .expectNoExceptions();
  }

  @Test
  void testDelayedOfferExchange() {
    new FixtureConfiguration()
      .given(b -> b.uac().invite())
      .given(b -> b.uas().trying())
      .given(b -> b.uas().sessionProgress())
      .given(b -> b.uas().ok(INVITE).withOffer())
      .given(b -> b.uac().ack().withAnswer())
      .given(b -> b.uac().bye())
      .when(b -> b.uas().ok(BYE))
      .expectStableNegotiation(1)
      .expectNoExceptions();
  }

  @Test
  void testDelayedOfferExchangeWithPrack() {
    new FixtureConfiguration($100REL)
      .given(b -> b.uac().invite())
      .given(b -> b.uas().trying())
      .given(b -> b.uas().sessionProgress())
      .given(b -> b.uas().sessionProgress().rseq().withOffer())
      .given(b -> b.uac().prack().rack().withAnswer())
      .given(b -> b.uas().ok(PRACK))
      .given(b -> b.uas().ok(INVITE))
      .when(b -> b.uac().ack())
      .expectStableNegotiation(1)
      .expectNoExceptions();
  }

  @Test
  void testDelayedReInviteWith1xxRel() {

    new FixtureConfiguration($100REL)
      .given(b -> b.uac().invite().withOffer())
      .given(b -> b.uas().ok(INVITE).withAnswer())
      .given(b -> b.uac().ack())
      .when(b -> b.uas().invite())
      .expectNegotiationState(UaRole.UAC, NegotiationState.STABLE)
      .expectThatState(a -> a.matches(state -> SignalQueries.offerRequired(state, UaRole.UAC), "UAC offer required"));
  }

  @Test
  void testDelayedReInviteNo1xxRel() {
    new FixtureConfiguration()
      .given(b -> b.uac().invite().withOffer())
      .given(b -> b.uas().ok(INVITE).withAnswer())
      .given(b -> b.uac().ack())
      .when(b -> b.uas().invite())
      .expectNegotiationState(UaRole.UAC, NegotiationState.STABLE)
      .expectThatState(a -> a.matches(state -> SignalQueries.offerRequired(state, UaRole.UAC), "UAC offer required"));
  }

  @Test
  void testReinviteNo1xxRel() {
    new FixtureConfiguration()
      .given(b -> b.uac().invite().withOffer())
      .given(b -> b.uas().ok(INVITE).withAnswer())
      .given(b -> b.uac().ack())
      // now reinvite
      .given(b -> b.uas().invite().withOffer())
      .given(b -> b.uac().ok(INVITE).withAnswer())
      .when(b -> b.uas().ack())
      .expectStableNegotiation(2);
  }

  @Test
  void testMultipleAnswersRejected() {
    new FixtureConfiguration($100REL)
      .given(b -> b.uac().invite().withOffer())
      .given(b -> b.uas().sessionProgress().rseq().withAnswer())
      .when(b -> b.uac().prack().rack().withAnswer())
      .expectException();
  }

  @Test
  void testMultipleOffersRejected() {
    new FixtureConfiguration($100REL)
      .given(b -> b.uac().invite().withOffer())
      .when(b -> b.uas().sessionProgress().rseq().withOffer())
      .expectException();
  }

  @Test
  void testUpdateInEarlyDialog() {
    new FixtureConfiguration($100REL)
      .given(b -> b.uac().invite().withOffer())
      .given(b -> b.uas().sessionProgress().rseq().withAnswer())
      .given(b -> b.uac().prack().rack())
      .given(b -> b.uas().ok(PRACK))
      .given(b -> b.uas().ok(INVITE))
      .when(b -> b.uac().ack())
      .expectStableNegotiation(1)
      .expectNoExceptions();
  }

}
