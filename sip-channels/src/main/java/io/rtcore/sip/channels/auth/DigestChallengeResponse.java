package io.rtcore.sip.channels.auth;

import java.util.Optional;

public record DigestChallengeResponse(
    String username,
    String realm,
    KnownDigestQualityOfProtection qop,
    String nonceCount,
    String response,
    String nonce,
    Optional<String> clientNonce) {

}
