package io.rtcore.sip.channels.auth;

import com.google.common.hash.Hashing;

import io.rtcore.sip.message.auth.StdDigestAlgo;
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
      .qop("auth") // ,auth-int
      .opaque(Long.toHexString(ts)));
    return res;
  }

}
