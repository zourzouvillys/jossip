package io.zrz.rtcore.useragent;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import com.google.common.collect.ImmutableListMultimap;

import io.rtcore.sip.channels.api.SipClientExchange.Event;
import io.rtcore.sip.channels.auth.DigestAuthUtils;
import io.rtcore.sip.channels.auth.DigestAuthorizer;
import io.rtcore.sip.channels.auth.DigestChallengeRequest;
import io.rtcore.sip.channels.auth.DigestChallengeResponse;
import io.rtcore.sip.channels.auth.DigestClientCredentials;
import io.rtcore.sip.channels.auth.ImmutableDigestContext;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipHeaderId;
import io.rtcore.sip.common.iana.SipMethodId;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.message.auth.headers.DigestCredentials;

public class SipAuthContext implements SipClientAuthProvider {

  private final String username;
  private final String password;
  private final String clientNonce;
  private ImmutableListMultimap<String, DigestChallengeRequest> state;

  SipAuthContext(String username, String password) {
    this.username = username;
    this.password = password;
    this.clientNonce = Long.toHexString(ThreadLocalRandom.current().nextLong());
  }

  @Override
  public void observe(Event msg) {

    ImmutableListMultimap<SipHeaderId, String> headers =
      msg.response()
        .headerLines()
        .stream()
        .collect(ImmutableListMultimap.toImmutableListMultimap(SipHeaderLine::headerId, SipHeaderLine::headerValues));

    ImmutableListMultimap<String, DigestChallengeRequest> realms =
      headers
        .get(StandardSipHeaders.WWW_AUTHENTICATE)
        .stream()
        .map(DigestCredentials::parseValue)
        .filter(e -> e != null)
        .map(DigestChallengeRequest::from)
        .collect(ImmutableListMultimap.toImmutableListMultimap(DigestChallengeRequest::realm, Function.identity()));

    this.state = realms;

  }

  @Override
  public List<SipHeaderLine> generate(SipMethodId method, String uri, String body) {

    if (this.state == null) {
      return List.of();
    }

    List<SipHeaderLine> lines = new ArrayList<>();

    for (Entry<String, Collection<DigestChallengeRequest>> item : state.asMap().entrySet()) {

      String realm = item.getKey();

      Collection<DigestChallengeRequest> challenges = item.getValue();

      DigestChallengeRequest challenge = challenges.iterator().next();

      ImmutableDigestContext context =
        ImmutableDigestContext.builder()
          .realm(realm)
          .method(method)
          .digestURI(uri)
          .entityHash(hashFunction -> hashFunction.hashString(body, StandardCharsets.UTF_8).toString())
          .build();
      ;

      DigestChallengeResponse res =
        DigestAuthorizer.generateResponse(
          context,
          new DigestClientCredentials(
            this.username,
            DigestAuthUtils.ha1(this.username,
              realm,
              this.password),
            1,
            this.clientNonce),
          challenge);

      lines.add(res.asHeader(challenge, context.digestURI(), StandardSipHeaders.AUTHORIZATION));

    }

    return lines;

  }

}
