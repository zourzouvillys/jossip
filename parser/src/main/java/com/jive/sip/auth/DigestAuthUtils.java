package com.jive.sip.auth;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.base.Joiner;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.jive.sip.auth.headers.Authorization;
import com.jive.sip.auth.headers.DigestCredentials;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.message.api.SipRequest;

public class DigestAuthUtils {

  private static final HashFunction md5 = Hashing.md5();
  private static final Joiner COLON = Joiner.on(":").useForNull("");

  public static String generateHA1(final String user, final String realm, final String password) {
    final String val = COLON.join(user, realm, password);
    return Hashing.md5().hashString(val, StandardCharsets.UTF_8).toString();
  }

  public static String generateHA2(final SipMethod method, final String uri) {
    final String val = COLON.join(method.toString(), uri);
    return md5.hashString(val, StandardCharsets.UTF_8).toString();
  }

  private static String generateResponse(final String HA1, final String nonce, final String nc, final String cnonce, final String qop, final String HA2) {
    final String val = COLON.join(HA1, nonce, nc, cnonce, qop, HA2);
    return md5.hashString(val, StandardCharsets.UTF_8).toString();
  }

  private static String generateResponse(final String HA1, final String nonce, final String HA2) {
    return md5.hashString(COLON.join(HA1, nonce, HA2), StandardCharsets.UTF_8).toString();
  }

  public static Authorization createResponse(final SipRequest req, final DigestCredentials auth, final String user, final String pass) {
    return createResponse(req.getMethod(), req.getUri().toString(), auth, generateNonce(), 1, user, pass);
  }

  public static DigestCredentials createResponse(
      final SipMethod method,
      final String uri,
      final DigestCredentials auth,
      final String cnonce,
      final int nc,
      final String user,
      final String pass) {

    final String HA1 = generateHA1(user, auth.realm(), pass);
    final String HA2 = generateHA2(method, uri);

    if (auth.qop() == null) {

      return auth
        .withUri(uri)
        .withUsername(user)
        .withResponse(generateResponse(HA1, auth.nonce(), HA2));

    }
    else if (auth.qop().equals("auth")) {

      final String mnc = String.format("%08x", nc).toUpperCase();

      return auth
        .withUri(uri)
        .withUsername(user)
        .withResponse(generateResponse(HA1, auth.nonce(), mnc, cnonce, "auth", HA2))
        .withCnonce(cnonce)
        .withNonceCount(nc);

    }

    throw new RuntimeException("Unknown qop: " + auth.qop());

  }

  private static String generateNonce() {
    final byte[] buffer = new byte[8];
    ThreadLocalRandom.current().nextBytes(buffer);
    return BaseEncoding.base64Url().omitPadding().encode(buffer);
  }

}
