package io.rtcore.sip.signaling.call;

import static io.rtcore.sip.message.message.api.SipMethod.INVITE;

import java.util.function.UnaryOperator;

import io.rtcore.sip.message.message.api.SipMethod;

public class FixtureCatalog {

  public static UnaryOperator<FixtureConfiguration> standardReliableInviteSetup() {
    return config -> {
      return config
        .given(b -> b.uac().invite().withOffer())
        .given(b -> b.uas().trying())
        .given(b -> b.uas().sessionProgress().rseq(9999).withAnswer())
        .given(b -> b.uac().prack().rack(9999))
        .given(b -> b.uas().ok(SipMethod.PRACK))
        .given(b -> b.uas().ok(INVITE))
        .given(b -> b.uac().ack());
    };
  }

  public static UnaryOperator<FixtureConfiguration> standardInviteSetup() {
    return config -> {
      return config
        .given(b -> b.uac().invite().withOffer())
        .given(b -> b.uas().trying())
        .given(b -> b.uas().sessionProgress().withAnswerPreview())
        .given(b -> b.uas().ok(INVITE).withAnswer())
        .given(b -> b.uac().ack());
    };
  }

}
