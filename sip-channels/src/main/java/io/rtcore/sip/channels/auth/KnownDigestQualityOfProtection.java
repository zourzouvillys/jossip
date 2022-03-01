package io.rtcore.sip.channels.auth;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Strings;

public enum KnownDigestQualityOfProtection {

  NONE,
  AUTH("auth"),
  AUTH_INT("auth-int"),;

  private final String token;

  private KnownDigestQualityOfProtection() {
    this.token = null;
  }

  private KnownDigestQualityOfProtection(String token) {
    this.token = Objects.requireNonNull(token);
  }

  public String token() {
    return switch (this) {
      case NONE -> throw new IllegalArgumentException();
      default -> this.token;
    };
  }

  private final static Map<String, KnownDigestQualityOfProtection> lookup =
    Stream.of(KnownDigestQualityOfProtection.values())
      .filter(e -> !Strings.isNullOrEmpty(e.token()))
      .collect(Collectors.toUnmodifiableMap(KnownDigestQualityOfProtection::token, Function.identity()));

  public static Optional<KnownDigestQualityOfProtection> fromToken(String token) {
    return Optional.ofNullable(lookup.get(token.toLowerCase()));
  }

}
