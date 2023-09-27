package io.rtcore.gateway.engine.grpc;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ProtocolStringList;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.reactivex.rxjava3.core.Single;
import io.rtcore.gateway.auth.proto.AuthorizationToken;
import io.rtcore.gateway.auth.proto.GetAuthorizationRequest;
import io.rtcore.gateway.auth.proto.Rx3DigestAuthServiceGrpc;
import io.rtcore.sip.channels.auth.DigestAuthorizer;
import io.rtcore.sip.channels.auth.DigestChallengeRequest;
import io.rtcore.sip.channels.auth.DigestChallengeResponse;
import io.rtcore.sip.channels.auth.DigestClientCredentials;
import io.rtcore.sip.channels.auth.ImmutableDigestContext;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.message.auth.headers.DigestCredentials;

public class DigestCredentialsServer extends Rx3DigestAuthServiceGrpc.DigestAuthServiceImplBase {

  private static final Logger LOG = LoggerFactory.getLogger(DigestCredentialsServer.class);

  @Override
  public Single<AuthorizationToken> getAuthorizationToken(final GetAuthorizationRequest request) {

    final ProtocolStringList challenges = request.getChallengesList();

    //
    final String[] p = request.getPrincipal().split(":", 2);
    final DigestClientCredentials creds =
      new DigestClientCredentials(
        p[0],
        p[1],
        1,
        Long.toHexString(ThreadLocalRandom.current().nextLong()));

    System.err.println(creds);

    //
    if (challenges.isEmpty()) {
      throw new StatusRuntimeException(Status.INVALID_ARGUMENT);
    }

    LOG.info("request: {}", request);

    final ImmutableDigestContext ctx =
      ImmutableDigestContext.builder()
        .realm(request.getRealm())
        .method(SipMethods.toMethodId(request.getMethod()))
        .digestURI(request.getUri())
        .entityHash(hashFunction -> hashFunction.hashString(Optional.ofNullable(request.getBody()).orElse(""), StandardCharsets.UTF_8).toString())
        .build();

    final AuthorizationToken.Builder rb = AuthorizationToken.newBuilder();

    for (final String challenge : challenges) {

      final DigestCredentials c = DigestCredentials.parseValue(challenge);

      if (c == null) {
        LOG.warn("failed to parse credentials");
        continue;
      }

      final DigestChallengeRequest req = DigestChallengeRequest.from(c);

      final DigestChallengeResponse er = DigestAuthorizer.generateResponse(ctx, creds, req);
      // DigestAuthService.rb.setAuthorization("");

      rb.addAuthorizations(er.asCredentials(req, request.getUri()).toString());

    }

    return Single.just(rb.build());

  }

}
