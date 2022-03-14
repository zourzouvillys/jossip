package io.rtcore.sip.channels.interceptors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import io.rtcore.sip.channels.api.SipChannel;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.errors.ProxyAuthenticationRequired;
import io.rtcore.sip.channels.interceptors.SipClientAuthInterceptor.Attempt;
import io.rtcore.sip.channels.interceptors.SipClientAuthInterceptor.Generator;
import io.rtcore.sip.channels.internal.SipCallOptions;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.message.auth.DigestAuthUtils;
import io.rtcore.sip.message.auth.headers.DigestCredentials;
import io.rtcore.sip.message.message.api.SipMethod;

class AuthGenerator implements Generator {

  private final List<DigestCredentials> challenges = new ArrayList<>();
  private final SipRequestFrame originalRequest;
  private final SipCallOptions options;
  private final SipChannel channel;
  private final String username;
  private final String password;

  public AuthGenerator(SipRequestFrame request, SipCallOptions options, SipChannel next, String username, String password) {
    this.originalRequest = request;
    this.options = options;
    this.channel = next;
    this.username = username;
    this.password = password;
  }

  @Override
  public Optional<Attempt> next(Throwable previous) {
    if (previous instanceof ProxyAuthenticationRequired) {
      return nextWithAuth((ProxyAuthenticationRequired) previous);
    }
    return Optional.empty();
  }

  private Optional<Attempt> nextWithAuth(ProxyAuthenticationRequired previous) {

    if (!challenges.isEmpty()) {
      return Optional.empty();
    }

    ArrayList<SipHeaderLine> headers = new ArrayList<>(this.originalRequest.headerLines());

    for (String line : previous.credentials()) {

      DigestCredentials challenge = DigestCredentials.parseValue(line);

      if (challenge != null) {
        challenges.add(challenge);
        // add challenge response if possible.
        headers.add(StandardSipHeaders.PROXY_AUTHORIZATION.ofLine(createDigestChallengeResponse(challenge).toString()));
      }

    }

    //
    return Optional.of(
      new Attempt(
        this.originalRequest.withHeaderLines(headers),
        this.options,
        this.channel));

  }

  private DigestCredentials createDigestChallengeResponse(DigestCredentials challenge) {
    String cnonce = Long.toHexString(ThreadLocalRandom.current().nextLong());
    DigestCredentials digestChallengeResponse =
      DigestAuthUtils.createResponse(
        SipMethod.fromString(this.originalRequest.initialLine().method().token()),
        this.originalRequest.initialLine().uri().toASCIIString(),
        challenge,
        cnonce,
        1,
        username,
        password);
    return digestChallengeResponse;
  }

}
