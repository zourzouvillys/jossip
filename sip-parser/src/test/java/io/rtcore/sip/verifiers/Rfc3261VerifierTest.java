package io.rtcore.sip.verifiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.message.api.ViaProtocol;
import io.rtcore.sip.message.processor.rfc3261.MutableSipRequest;
import io.rtcore.sip.message.uri.SipUri;

class Rfc3261VerifierTest {

  @Test
  void testThrowsIfAllHeadersMissing() {
    SipVerifyException ex =
      assertThrows(
        SipVerifyException.class,
        () -> new Rfc3261Verifier().verify(
          MutableSipRequest
            .createInvite(SipUri.parseString("sip:invalid.domain"))
            .build()));
    assertEquals("missing required headers (Via, To, From, Call-ID, CSeq, Max-Forwards)", ex.getMessage());
  }

  @Test
  void testThrowsIfMissingCallIdHeader() {
    SipVerifyException ex =
      assertThrows(
        SipVerifyException.class,
        () -> new Rfc3261Verifier().verify(
          MutableSipRequest
            .createInvite(SipUri.parseString("sip:invalid.domain"))
            .to(SipUri.parseString("sip:test"))
            .from(SipUri.parseString("sip:test"), "aaa")
            .via(ViaProtocol.TCP, HostAndPort.fromHost("localhost"), "", true)
            .maxForwards(123)
            .cseq(1)
            .build()));
    assertEquals("missing required Call-ID header", ex.getMessage());
  }

}
