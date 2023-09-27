package io.rtcore.sip.channels.auth;

import java.util.Optional;

import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipHeaderId;
import io.rtcore.sip.message.auth.headers.DigestCredentials;
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

  public SipHeaderLine asHeader(final DigestChallengeRequest req, final String digestURI, final SipHeaderId header) {
    return SipHeaderLine.of(header, this.asCredentials(req, digestURI).toString());
  }

  public DigestCredentials asCredentials(final DigestChallengeRequest req, final String digestURI) {

    final ImmutableDigestValues values =
      DigestValues.builder()
        .realm(this.realm)
        .nonce(this.nonce)
        .algorithm(req.algo())
        .qop(this.qop.token())
        .uri(digestURI)
        .opaque(req.opaque())
        .username(this.username)
        .cnonce(this.clientNonce)
        .nonceCount(Integer.parseInt(this.nonceCount))
        .response(this.response)
        .build();

    return values.asCredentials();
  }

}
