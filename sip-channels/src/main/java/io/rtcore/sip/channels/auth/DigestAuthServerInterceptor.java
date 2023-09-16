package io.rtcore.sip.channels.auth;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

import com.google.common.hash.Hashing;

import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipServerExchange;
import io.rtcore.sip.channels.api.SipServerExchange.Listener;
import io.rtcore.sip.channels.api.SipServerExchangeHandler;
import io.rtcore.sip.channels.api.SipServerExchangeInterceptor;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.common.iana.SipStatusCodes;
import io.rtcore.sip.frame.SipFrameUtils;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.frame.SipResponseFrame;
import io.rtcore.sip.message.auth.StdDigestAlgo;

public class DigestAuthServerInterceptor implements SipServerExchangeInterceptor<SipRequestFrame, SipResponseFrame> {

  private final SipAuthRole role;
  private final BiFunction<SipRequestFrame, SipAttributes, String> realm;
  private DigestAuthorizer authorizer;

  public DigestAuthServerInterceptor(SipAuthRole role, String realm, DigestCredentialsStore store) {
    this(role, (req, attr) -> realm, store);
  }

  public DigestAuthServerInterceptor(SipAuthRole role, BiFunction<SipRequestFrame, SipAttributes, String> realm, DigestCredentialsStore store) {
    this.role = role;
    this.realm = realm;
    this.authorizer = new DigestAuthorizer(store);
  }

  @Override
  public Listener interceptExchange(
      SipServerExchange<SipRequestFrame, SipResponseFrame> exchange,
      SipAttributes attributes,
      SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> next) {

    if (exchange.request().initialLine().method() == SipMethods.ACK) {
      return next.startExchange(exchange, attributes);
    }
    else if (exchange.request().initialLine().method() == SipMethods.CANCEL) {
      return next.startExchange(exchange, attributes);
    }

    return doAuth(exchange, attributes, next);

  }

  private Listener doAuth(
      SipServerExchange<SipRequestFrame, SipResponseFrame> exchange,
      SipAttributes attributes,
      SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> next) {

    //
    SipRequestFrame req = exchange.request();

    DigestContext authctx =
      DigestAuthUtils.createDigestContext(
        req,
        this.realm.apply(exchange.request(), attributes));

    //
    Optional<DigestChallengeResponse> authres =
      DigestAuthUtils.extractResponse(
        req,
        role.challengeResponseHeader(),
        authctx.realm());

    if (authres.isEmpty()) {

      long ts = System.currentTimeMillis();
      String nonce = Hashing.farmHashFingerprint64().hashLong(ts).toString();

      // we need to create a challenge.
      DigestChallengeRequest challenge =
        new DigestChallengeRequest(
          authctx.realm(),
          nonce,
          false,
          Long.toHexString(ts),
          StdDigestAlgo.MD5,
          KnownDigestQualityOfProtection.AUTH);

      exchange.onNext(
        SipFrameUtils.createResponse(
          req,
          role.statusCode(),
          List.of(challenge.asHeader(role.challengeRequestHeader()))));

      exchange.onComplete();

      return null;

    }

    // we have auth credentials, so verify it.
    try {

      Optional<SipPrinicpal> authout =
        this.authorizer.verifyResponse(
          authctx,
          authres.get()).toCompletableFuture().get();

      if (!authout.isPresent()) {

        // DigestChallengeRequest challenge = new DigestChallengeRequest(authctx.realm());

        exchange.onNext(
          SipFrameUtils.createResponse(
            req,
            SipStatusCodes.UNAUTHORIZED,
            List.of()));

        // exchange.onNext(
        // SipFrameUtils.createResponse(
        // req,
        // role.statusCode(),
        // List.of(challenge.asHeader(role.challengeRequestHeader()))));

        exchange.onComplete();

        return null;

      }

      // todo: remove the Proxy-Authorization header so it is not forwarded downstream.
      System.err.println(authout);

    }
    catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }

    return next.startExchange(exchange, attributes);

  }

}
