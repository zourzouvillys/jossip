package io.rtcore.sip.iana;

import java.util.Arrays;

import com.google.common.collect.ImmutableMap;

//@formatter:off
// csvq -q -N -f FIXED 'SELECT "/** " || Reference || " */\n" || Methods || "(\"" || Methods || "\")," FROM `sip-parameters-6.csv`'
//@formatter:on

public enum SipMethods {

  /** [RFC3261] */
  ACK("ACK"),
  /** [RFC3261] */
  BYE("BYE"),
  /** [RFC3261] */
  CANCEL("CANCEL"),
  /** [RFC6086] */
  INFO("INFO"),
  /** [RFC3261][RFC6026] */
  INVITE("INVITE"),
  /** [RFC3428] */
  MESSAGE("MESSAGE"),
  /** [RFC6665] */
  NOTIFY("NOTIFY"),
  /** [RFC3261] */
  OPTIONS("OPTIONS"),
  /** [RFC3262] */
  PRACK("PRACK"),
  /** [RFC3903] */
  PUBLISH("PUBLISH"),
  /** [RFC3515] */
  REFER("REFER"),
  /** [RFC3261] */
  REGISTER("REGISTER"),
  /** [RFC6665] */
  SUBSCRIBE("SUBSCRIBE"),
  /** [RFC3311] */
  UPDATE("UPDATE"),
  ;

  private final String method;

  SipMethods(String method) {
    this.method = method;
  }

  public String token() {
    return this.method;
  }

  public boolean equals(CharSequence in) {
    return CharSequence.compare(in, method) == 0;
  }

  public boolean equals(SipMethods in) {
    return in == this;
  }

  /// ---

  public static SipMethods fromToken(CharSequence token) {
    return tokenToValue.get(token);
  }

  private static final ImmutableMap<String, SipMethods> tokenToValue;
  static {
    tokenToValue = Arrays.stream(values()).collect(ImmutableMap.toImmutableMap(e -> e.name(), e -> e));
  }
  
}
