package io.rtcore.sip.verifiers;

import static io.rtcore.sip.common.iana.StandardSipHeaders.CALL_ID;
import static io.rtcore.sip.common.iana.StandardSipHeaders.CSEQ;
import static io.rtcore.sip.common.iana.StandardSipHeaders.FROM;
import static io.rtcore.sip.common.iana.StandardSipHeaders.MAX_FORWARDS;
import static io.rtcore.sip.common.iana.StandardSipHeaders.TO;
import static io.rtcore.sip.common.iana.StandardSipHeaders.VIA;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import io.rtcore.sip.common.iana.SipHeaderId;
import io.rtcore.sip.message.message.SipMessage;
import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.SipResponse;

/**
 * verifies some basic RFC 3261 requirements.
 *
 * UAC:
 *
 * A valid SIP request formulated by a UAC MUST, at a minimum, contain the following header fields:
 * To, From, CSeq, Call-ID, Max-Forwards, and Via; all of these header fields are mandatory in all
 * SIP requests. These six header fields are the fundamental building blocks of a SIP message, as
 * they jointly provide for most of the critical message routing services including the addressing
 * of messages, the routing of responses, limiting message propagation, ordering of messages, and
 * the unique identification of transactions. These header fields are in addition to the mandatory
 * request line, which contains the method, Request-URI, and SIP version.
 *
 * <ul>
 * <li>mandatory headers are present</li>
 * <li>top via contains magic cookie.</li>
 * </ul>
 *
 * @author theo
 *
 */

public class Rfc3261Verifier {

  public Rfc3261Verifier() {
  }

  private static final ImmutableSet<SipHeaderId> requiredRequestHeaders =
    ImmutableSet.of(VIA, TO, FROM, CALL_ID, CSEQ, MAX_FORWARDS);

  private static final ImmutableSet<SipHeaderId> requiredResponseHeaders =
    ImmutableSet.of(VIA, TO, FROM, CALL_ID, CSEQ);

  public static final Set<SipHeaderId> requiredRequestHeaders() {
    return requiredRequestHeaders;
  }

  public static final Set<SipHeaderId> requiredResponseHeaders() {
    return requiredResponseHeaders;
  }

  public void verify(final SipMessage msg) {
    if (msg instanceof SipRequest) {
      this.verifyRequest((SipRequest) msg);
    }
    else if (msg instanceof SipResponse) {
      this.verifyResponse((SipResponse) msg);
    }
    else {
      throw new IllegalArgumentException();
    }
  }

  private void verifyRequest(final SipRequest req) {
    final ImmutableSet<SipHeaderId> missing =
      requiredRequestHeaders().stream().filter(hdr -> req.getHeaders(hdr.headerNames()).isEmpty()).collect(ImmutableSet.toImmutableSet());
    if (!missing.isEmpty()) {
      if (missing.size() == 1) {
        throw new SipVerifyException("missing required " + missing.iterator().next().prettyName() + " header");
      }
      throw new SipVerifyException("missing required headers (" + missing.stream().map(SipHeaderId::prettyName).collect(Collectors.joining(", ")) + ")");
    }
  }

  private void verifyResponse(final SipResponse res) {
  }

}
