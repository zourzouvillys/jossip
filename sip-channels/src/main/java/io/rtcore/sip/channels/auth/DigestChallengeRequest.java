package io.rtcore.sip.channels.auth;

import com.google.common.hash.Hashing;

import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipHeaderId;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.message.auth.StdDigestAlgo;
import io.rtcore.sip.message.auth.headers.DigestCredentials;
import io.rtcore.sip.message.auth.headers.ImmutableDigestValues;
import io.rtcore.sip.message.processor.rfc3261.MutableSipResponse;

public record DigestChallengeRequest(String realm) implements DigestChallengeResult {

  public MutableSipResponse applyTo(MutableSipResponse res) {

    long ts = System.currentTimeMillis();
    String nonce = Hashing.farmHashFingerprint64().hashLong(ts).toString();
    boolean stale = false;
    //

    res.proxyAuthenticate(b -> b
      .realm(realm)
      .nonce(nonce)
      .stale(stale)
      .algorithm(StdDigestAlgo.MD5)
      .qop("auth")
      .opaque(Long.toHexString(ts)));

    return res;
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
