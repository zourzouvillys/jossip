package io.rtcore.sip.channels.auth;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.google.common.base.Joiner;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import io.rtcore.sip.message.auth.StdDigestAlgo;
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
    long ts = System.currentTimeMillis();
    String nonce = Hashing.farmHashFingerprint64().hashLong(ts).toString();
    return CompletableFuture.completedStage(
      new DigestChallengeRequest(
        ctx.realm(),
        nonce,
        false,
        Long.toHexString(ts),
        StdDigestAlgo.MD5,
        KnownDigestQualityOfProtection.AUTH));
  }

  /**
   * 
   * 
   * @param ctx
   * @param res
   * @return
   */

  @Override
  public CompletionStage<Optional<SipPrinicpal>> verifyResponse(DigestContext ctx, DigestChallengeResponse res) {

    // require MD5 ...
    @SuppressWarnings("deprecation")
    HashFunction hashFunction = Hashing.md5();

    String ha2 =
      ha2(
        ctx.method().token(),
        ctx.digestURI(),
        ctx.entityHash().apply(hashFunction),
        hashFunction,
        res.qop());

    //
    CompletionStage<Optional<SipPrinicpal>> result =
      store
        .ha1(res.username(), res.realm())
        .thenApply(creds -> validate(ha2, hashFunction, res, creds)
          .map(e -> new SipPrinicpal(e.username(), e.realm(), e.attributes())));

    return result;

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

  public
      Optional<Credential>
      validate(
          String method,
          String digestURI,
          String entityHash,
          HashFunction hashFunction,
          DigestChallengeResponse res,
          CredentialSet ha1s) {

    String ha2 = ha2(method, digestURI, entityHash, hashFunction, res.qop());

    for (Credential credential : ha1s.credentials()) {

      String response =
        calculateResponse(
          hashFunction,
          credential.ha1(),
          ha2,
          res);

      if (response.equals(res.response())) {
        return Optional.of(credential);
      }
    }

    return Optional.empty();

  }

  public Optional<Credential> validate(String ha2, HashFunction hashFunction, DigestChallengeResponse res, CredentialSet ha1s) {

    for (Credential credential : ha1s.credentials()) {

      String response =
        calculateResponse(
          hashFunction,
          credential.ha1(),
          ha2,
          res);

      if (response.equals(res.response())) {
        return Optional.of(credential);
      }

    }
    return Optional.empty();
  }

  public static final DigestChallengeResponse generateResponse(
      DigestContext ctx,
      DigestClientCredentials creds,
      DigestChallengeRequest req) {

    // require MD5 ...
    @SuppressWarnings("deprecation")
    HashFunction hashFunction = Hashing.md5();

    String ha2 =
      ha2(
        ctx.method().token(),
        ctx.digestURI(),
        ctx.entityHash().apply(hashFunction),
        hashFunction,
        req.qop());

    String username = creds.username();
    String ha1 = creds.ha1();
    String nonceCount = toNonceCountString(creds.nonceCount());
    String clientNonce = creds.clientNonce();

    String response =
      calculateResponse(
        hashFunction,
        ha1,
        ha2,
        nonceCount,
        clientNonce,
        req.nonce(),
        req.qop());

    return new DigestChallengeResponse(
      username,
      ctx.realm(),
      req.qop(),
      nonceCount,
      response,
      req.nonce(),
      Optional.ofNullable(clientNonce).filter(e -> !e.isEmpty()));

  }

  private static String toNonceCountString(long nonceCount) {
    return String.format("%08x", nonceCount);
  }

  private static String calculateResponse(
      HashFunction hashFunction,
      String ha1,
      String ha2,
      String nonceCount,
      String clientNonce,
      String nonce,
      KnownDigestQualityOfProtection qop) {

    ArrayList<String> parts = new ArrayList<>();

    parts.add(ha1);
    parts.add(nonce);

    switch (qop) {
      case NONE:
      case AUTH:
      case AUTH_INT:
        parts.add(nonceCount);
        parts.add(clientNonce);
        parts.add(qop.token());
        break;
    }

    parts.add(ha2);

    return digestHash(hashFunction, parts, "response");

  }

  private static String digestHash(HashFunction hashFunction, Iterable<String> parts, String label) {
    String in = Joiner.on(':').join(parts);
    String out = hashFunction.hashString(in, StandardCharsets.UTF_8).toString();
    return out;
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

  private static String ha2(String method, String digestURI, String entityHash, HashFunction hashFunction, KnownDigestQualityOfProtection qop) {
    switch (qop) {
      case NONE:
      case AUTH:
        return digestHash(hashFunction, List.of(method, digestURI), "HA2");
      case AUTH_INT:
        return digestHash(hashFunction, List.of(method, digestURI, entityHash), "HA2");
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

  public Optional<DigestCredentials> extractCredentials(List<Authorization> auths, String realm) {
    return auths.stream()
      .map(auth -> auth.as(DigestCredentials.class))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .filter(creds -> realmMatch(realm, creds.realm()))
      .findFirst();
  }

  private boolean realmMatch(String wanted, String provided) {
    return wanted.equalsIgnoreCase(provided);
  }

}
