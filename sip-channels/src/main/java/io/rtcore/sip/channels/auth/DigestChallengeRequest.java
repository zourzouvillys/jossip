package io.rtcore.sip.channels.auth;

import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipHeaderId;
import io.rtcore.sip.message.auth.StdDigestAlgo;
import io.rtcore.sip.message.auth.headers.DigestCredentials;
import io.rtcore.sip.message.auth.headers.ImmutableDigestValues;
import io.rtcore.sip.message.processor.rfc3261.MutableSipResponse;

public record DigestChallengeRequest(

    String realm,
    String nonce,
    boolean stale,
    String opaque,
    StdDigestAlgo algo,
    KnownDigestQualityOfProtection qop

) implements DigestChallengeResult {

  public MutableSipResponse applyTo(MutableSipResponse res) {
    return res.proxyAuthenticate(b -> b
      .realm(realm)
      .nonce(nonce)
      .stale(stale)
      .algorithm(algo)
      .qop(qop.token())
      .opaque(opaque));
  }

  public SipHeaderLine asHeader(SipHeaderId header) {
    ImmutableDigestValues values =
      DigestCredentials.builder()
        .realm(realm)
        .nonce(nonce)
        .stale(stale)
        .algorithm(algo)
        .qop(qop.token())
        .opaque(opaque)
        .build();
    return SipHeaderLine.of(header, values.asCredentials().toString());
  }

  public static DigestChallengeRequest from(DigestCredentials creds) {

    return new DigestChallengeRequest(

      // String realm,
      creds.realm(),

      // String nonce,
      creds.nonce(),

      // boolean stale,
      creds.stale(),

      // String opaque,
      creds.opaque(),

      // StdDigestAlgo algo,
      StdDigestAlgo.fromToken(creds.algorithm()),

      // KnownDigestQualityOfProtection qop
      KnownDigestQualityOfProtection.fromToken(creds.qop()).orElse(KnownDigestQualityOfProtection.NONE)

    //
    );

  }

  public SipHeaderLine asHeader(SipHeaderId header) {

    long ts = System.currentTimeMillis();
    String nonce = Hashing.farmHashFingerprint64().hashLong(ts).toString();
    boolean stale = false;

    ImmutableDigestValues values =
      DigestCredentials.builder()
        .realm(realm)
        .nonce(nonce)
        .stale(stale)
        .algorithm(StdDigestAlgo.MD5)
        .qop("auth")
        .opaque(Long.toHexString(ts))
        .build();

    return SipHeaderLine.of(header, values.asCredentials().toString());
  }

}
