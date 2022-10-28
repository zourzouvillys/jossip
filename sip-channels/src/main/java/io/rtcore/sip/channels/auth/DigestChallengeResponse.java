package io.rtcore.sip.channels.auth;

import java.util.Optional;

import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipHeaderId;
import io.rtcore.sip.message.auth.headers.DigestValues;
import io.rtcore.sip.message.auth.headers.ImmutableDigestValues;

public record DigestChallengeResponse(
    String username,
    String realm,
    KnownDigestQualityOfProtection qop,
    String nonceCount,
    String response,
    String nonce,
    Optional<String> clientNonce) {

  public SipHeaderLine asHeader(DigestChallengeRequest req, String digestURI, SipHeaderId header) {

    ImmutableDigestValues values =
      DigestValues.builder()
        .realm(realm)
        .nonce(nonce)
        .algorithm(req.algo())
        .qop(qop.token())
        .uri(digestURI)
        .opaque(req.opaque())
        .username(username)
        .cnonce(clientNonce)
        .nonceCount(Integer.parseInt(nonceCount))
        .response(response)
        .build();

    return SipHeaderLine.of(header, values.asCredentials().toString());
  }

}
