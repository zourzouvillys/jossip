package io.rtcore.sip.channels.auth;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import io.reactivex.rxjava3.core.Maybe;
import io.rtcore.sip.message.auth.headers.Authorization;
import io.rtcore.sip.message.auth.headers.DigestCredentials;
import io.rtcore.sip.message.message.SipRequest;

/**
 * authorizer which performs Digest authentication.
 */

public class DigestAuthorizer implements DigestAuthService {

  private DigestCredentialsStore store;

  /**
   * 
   */

  public DigestAuthorizer(DigestCredentialsStore store) {
    this.store = Objects.requireNonNull(store);
  }

  /**
   * 
   * @param ctx
   * 
   * @return a digest challenge request
   */

  @Override
  public CompletionStage<DigestChallengeRequest> calculateChallenge(DigestContext ctx) {
    return CompletableFuture.failedStage(new IllegalArgumentException());
  }

  /**
   * 
   * 
   * @param ctx
   * @param res
   * @return
   */

  @Override
  public CompletionStage<DigestChallengeResult> verifyResponse(DigestContext ctx, DigestChallengeResponse res) {

    HashFunction hashFunction = Hashing.md5();

    //
    Maybe<Credential> ha1 = store.ha1(res.username(), res.realm());

    //
    String ha2 =
      ha2(
        ctx.method().token(),
        ctx.digestURI(),
        ctx.entityHash().apply(hashFunction),
        hashFunction,
        res.qop());

    return CompletableFuture.failedStage(new IllegalArgumentException());

  }

  /**
   * 
   * @param method
   * @param digestURI
   * @param entityHash
   * @param hashFunction
   * @param res
   * @param ha1s
   * @return
   */

  public boolean validate(String method, String digestURI, String entityHash, HashFunction hashFunction, DigestChallengeResponse res, Credential ha1s) {

    String ha2 = ha2(method, digestURI, entityHash, hashFunction, res.qop());

    for (String ha1 : ha1s.ha1s()) {

      String response = calculateResponse(hashFunction, ha1, ha2, res);

      if (response.equals(res.response())) {
        return true;
      }

    }

    return false;

  }

  private String calculateResponse(HashFunction hashFunction, String ha1, String ha2, DigestChallengeResponse res) {

    ArrayList<String> parts = new ArrayList<>();

    parts.add(ha1);
    parts.add(res.nonce());

    switch (res.qop()) {
      case NONE:
      case AUTH:
      case AUTH_INT:
        parts.add(res.nonceCount());
        parts.add(res.clientNonce().orElse(""));
        parts.add(res.qop().token());
        break;
    }

    parts.add(ha2);

    return hashFunction.hashString(
      Joiner.on(':').join(parts),
      StandardCharsets.UTF_8).toString();

  }

  private String ha2(String method, String digestURI, String entityHash, HashFunction hashFunction, KnownDigestQualityOfProtection qop) {
    switch (qop) {
      case AUTH:
        return hashFunction.hashString(String.format("%s:%s", method, digestURI), StandardCharsets.UTF_8).toString();
      case AUTH_INT:
        return hashFunction.hashString(String.format("%s:%s:%s", method, digestURI, entityHash), StandardCharsets.UTF_8).toString();
      default:
        throw new IllegalArgumentException();
    }

  }

  public static String bodyHash(HashFunction hashFunction, SipRequest req) {
    if (req.body() == null || req.body().length == 0) {
      return hashFunction.hashBytes(new byte[0]).toString();
    }
    return hashFunction.hashBytes(req.body()).toString();
  }

  private List<DigestCredentials> extractDigestResponse(List<Authorization> auths, String realm) {
    return auths.stream()
      .map(auth -> auth.as(DigestCredentials.class))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .filter(creds -> realmMatch(realm, creds.realm()))
      .collect(Collectors.toList());
  }

  private boolean realmMatch(String wanted, String provided) {
    return wanted.equalsIgnoreCase(provided);
  }

}
