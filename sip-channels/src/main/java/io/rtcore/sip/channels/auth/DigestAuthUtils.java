package io.rtcore.sip.channels.auth;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import io.rtcore.sip.channels.errors.ClientFailure;
import io.rtcore.sip.common.iana.SipHeaderId;
import io.rtcore.sip.frame.SipFrameUtils;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.message.auth.StdDigestAlgo;
import io.rtcore.sip.message.auth.headers.DigestCredentials;
import io.rtcore.sip.message.message.SipResponseStatus;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.AuthorizationParser;

public class DigestAuthUtils {

  /**
   * a request can contain a single DigestChallengeResponse.
   */

  public static Optional<DigestChallengeResponse> extractResponse(SipRequestFrame req, SipHeaderId header, String realm) {

    List<DigestChallengeResponse> res = extractDigestResponse(SipFrameUtils.headerValues(req.headerLines(), header), realm);

    if (res.isEmpty()) {
      return Optional.empty();
    }
    else if (res.size() > 1) {
      throw new ClientFailure(SipResponseStatus.BAD_REQUEST.withReason("multiple Digest responses for same realm"));
    }

    return Optional.of(res.get(0));

  }

  public static DigestContext createDigestContext(SipRequestFrame req, String realm) {
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

  private static Stream<DigestChallengeResponse> extractDigestResponse(Stream<String> headerValues, String realm) {
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
          KnownDigestQualityOfProtection.fromToken(creds.qop()).orElseThrow(() -> new IllegalArgumentException(creds.qop())),
          creds.nc(),
          creds.response(),
          creds.nonce(),
          Optional.ofNullable(creds.cnonce())));
  }

  private static List<DigestChallengeResponse> extractDigestResponse(Iterable<String> headerValues, String realm) {
    return extractDigestResponse(Lists.newArrayList(headerValues).stream(), realm).collect(Collectors.toUnmodifiableList());
  }

  public static Stream<DigestChallengeRequest> extractDigestRequest(Stream<String> headerValues, String realm) {

    return headerValues
      .map(String::strip)
      .filter(value -> value.toLowerCase().startsWith("digest "))
      .map(AuthorizationParser.INSTANCE::parseValue)
      .map(auth -> auth.as(DigestCredentials.class).orElse(null))
      .filter(auth -> auth != null)
      .filter(creds -> creds.realm().equalsIgnoreCase(realm))
      .map(
        creds -> new DigestChallengeRequest(

          // String realm,
          creds.realm(),

          // String nonce,
          creds.nonce(),

          // boolean stale,
          creds.stale(),

          // String opaque,
          creds.opaque(),

          // StdDigestAlgo algo,
          StdDigestAlgo.valueOf(creds.algorithm()),

          // KnownDigestQualityOfProtection qop
          KnownDigestQualityOfProtection.fromToken(creds.qop()).orElse(KnownDigestQualityOfProtection.NONE)

        //
        ));
  }

  public static String ha1(String username, String realm, String password) {
    HashFunction hashFunction = Hashing.md5();
    return hashFunction.hashString(String.format("%s:%s:%s", username, realm, password), StandardCharsets.UTF_8).toString();
  }

}
