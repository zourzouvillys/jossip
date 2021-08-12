package io.rtcore.sip.common.iana;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * well known standard sip transport names.
 *
 * https://www.iana.org/assignments/sip-parameters/sip-parameters.xhtml#sip-transport
 */

public enum StandardSipTransportName {

  UDP, // [RFC3261]
  TCP, // [RFC3261]
  TLS, // [RFC3261]
  SCTP, // [RFC3261][RFC4168]
  TLS_SCTP("TLS-SCTP"), // [RFC4168]
  WS, // [RFC7118]
  WSS,  // [RFC7118]
  ;

  private final String id;

  StandardSipTransportName() {
    this.id = this.name();
  }

  StandardSipTransportName(final String name) {
    this.id = name;
  }

  public String id() {
    return this.id;
  }

  @Override
  public String toString() {
    return this.id;
  }

  public Optional<StandardSipTransportName> of(final String name) {
    // all are uppercase, so we can just force to uppercase to confirm.
    return Optional.ofNullable(lookup.get(name.toUpperCase()));
  }

  private static final Map<String, StandardSipTransportName> lookup =
      Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(StandardSipTransportName::id, Function.identity()));

}
