package io.rtcore.sip.message.auth;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// algorithm = "algorithm" EQUAL ( "MD5" / "MD5-sess" / "SHA-256" / "SHA-256-sess" / "SHA-512-256" /
// "SHA-512-256-sess" / token )

public enum StdDigestAlgo implements DigestAlgo {

  MD5,
  SHA_256,
  SHA_512_256,

  MD5_sess,
  SHA_256_sess,
  SHA_512_256_sess,
  ;

  private static final Map<String, StdDigestAlgo> values =
    Arrays.stream(values())
      .collect(
        Collectors.toUnmodifiableMap(
          e -> e.algId().toLowerCase(),
          Function.identity()));

  private final String id;

  private StdDigestAlgo() {
    this.id = name().replace('_', '-');
  }

  @Override
  public String algId() {
    return this.id;
  }

  public static StdDigestAlgo fromToken(String algId) {
    return values.get(algId.toLowerCase());
  }

}
