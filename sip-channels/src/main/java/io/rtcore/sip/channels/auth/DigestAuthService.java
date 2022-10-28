package io.rtcore.sip.channels.auth;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import io.rtcore.sip.channels.api.SipRequestFrame;
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

  CompletionStage<Optional<SipPrinicpal>> verifyResponse(DigestContext ctx, DigestChallengeResponse res);

  /**
   * 
   */

  static DigestContext createDigestContext(SipRequest req, String realm) {
    return ImmutableDigestContext.builder()
      .realm(realm)
      .method(req.method().methodId())
      .digestURI(req.uri().toString())
      .entityHash(hashFunction -> hashFunction.hashBytes(Optional.ofNullable(req.body()).orElse(new byte[0])).toString())
      .build();
  }

  static DigestContext createDigestContext(SipRequestFrame req, String realm) {
    return ImmutableDigestContext.builder()
      .realm(realm)
      .method(req.initialLine().method())
      .digestURI(req.initialLine().uri().toString())
      .entityHash(hashFunction -> hashFunction.hashString(req.body().orElse(""), StandardCharsets.UTF_8).toString())
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

  public static Stream<DigestChallengeResponse> extractDigestResponse(Stream<String> headerValues, String realm) {
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

  public static Stream<DigestChallengeResponse> extractDigestResponse(Iterable<String> headerValues, String realm) {
    return extractDigestResponse(Lists.newArrayList(headerValues), realm);
  }

}
