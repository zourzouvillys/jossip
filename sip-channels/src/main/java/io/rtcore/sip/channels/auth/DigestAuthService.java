package io.rtcore.sip.channels.auth;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.rtcore.sip.channels.errors.BadRequest;
import io.rtcore.sip.common.iana.SipHeaderId;
import io.rtcore.sip.message.auth.headers.DigestCredentials;
import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.AuthorizationParser;

public interface DigestAuthService {

  /**
   * 
   * @param ctx
   * 
   * @return a digest challenge request
   */

  CompletionStage<DigestChallengeRequest> calculateChallenge(DigestContext ctx);

  /**
   * 
   * @param ctx
   * @param res
   * 
   * @return
   */

  CompletionStage<DigestChallengeResult> verifyResponse(DigestContext ctx, DigestChallengeResponse res);

  /**
   * fails if the response contains more than one auth header with the same realm.
   */

  default CompletionStage<? extends DigestChallengeResult> verifyResponse(SipRequest req, SipHeaderId header, String realm) {

    DigestContext ctx = createDigestContext(req, realm);

    List<DigestChallengeResponse> digestChallengeResponse =
      extractDigestResponse(req.headerValues(header), realm)
        .collect(Collectors.toList());

    if (digestChallengeResponse.isEmpty()) {
      return this.calculateChallenge(ctx);
    }
    else if (digestChallengeResponse.size() > 1) {
      return CompletableFuture.failedStage(new BadRequest());
    }

    return verifyResponse(ctx, digestChallengeResponse.get(0));

  }

  /**
   * 
   * @param req
   * @return
   */

  static DigestContext createDigestContext(SipRequest req, String realm) {
    return ImmutableDigestContext.builder()
      .realm(realm)
      .method(req.method().methodId())
      .digestURI(req.uri().toString())
      .entityHash(hashFunction -> hashFunction.hashBytes(Optional.ofNullable(req.body()).orElse(new byte[0])).toString())
      .build();
  }

  /**
   * extracts a digest response, throwing an error if the header value is Digest and the realm is
   * ours but contains invalid qop.
   * 
   * this is important to avoid potential security issues and passthru risks.
   * 
   * @param headerValues
   * @param header
   * @param realm
   * @return
   */

  static Stream<DigestChallengeResponse> extractDigestResponse(Stream<String> headerValues, String realm) {
    return headerValues
      .map(String::strip)
      .filter(value -> value.toLowerCase().startsWith("digest "))
      .map(AuthorizationParser.INSTANCE::parseValue)
      .map(auth -> auth.as(DigestCredentials.class).orElse(null))
      .filter(auth -> auth != null)
      .filter(creds -> creds.realm().equalsIgnoreCase(realm))
      .map(
        creds -> new DigestChallengeResponse(
          creds.username(),
          creds.realm(),
          KnownDigestQualityOfProtection.fromToken(creds.qop()).orElseThrow(),
          creds.nc(),
          creds.response(),
          creds.nonce(),
          Optional.ofNullable(creds.cnonce())));
  }

}
