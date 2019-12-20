package io.rtcore.sip.message.message.api;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.rtcore.sip.message.base.api.Token;

/**
 * Represents a SIP method.
 *
 * 
 */

public final class SipMethod {

  // Make sure you keep in sync with SipMethod.METHOD_MAP with the static constructor.
  // TODO: fix this requirement. add a KnownSipMethods? -- tpz

  // ACK [RFC3261]
  public static final SipMethod ACK = new SipMethod("ACK");
  // BYE [RFC3261]
  public static final SipMethod BYE = new SipMethod("BYE");
  // CANCEL [RFC3261]
  public static final SipMethod CANCEL = new SipMethod("CANCEL");
  // INFO [RFC6086]
  public static final SipMethod INFO = new SipMethod("INFO");
  // INVITE [RFC3261][RFC6026]
  public static final SipMethod INVITE = new SipMethod("INVITE");
  // MESSAGE [RFC3428]
  public static final SipMethod MESSAGE = new SipMethod("MESSAGE");
  // NOTIFY [RFC6665]
  public static final SipMethod NOTIFY = new SipMethod("NOTIFY");
  // OPTIONS [RFC3261]
  public static final SipMethod OPTIONS = new SipMethod("OPTIONS");
  // PRACK [RFC3262]
  public static final SipMethod PRACK = new SipMethod("PRACK");
  // PUBLISH [RFC3903]
  public static final SipMethod PUBLISH = new SipMethod("PUBLISH");
  // REFER [RFC3515]
  public static final SipMethod REFER = new SipMethod("REFER");
  // REGISTER [RFC3261]
  public static final SipMethod REGISTER = new SipMethod("REGISTER");
  // SUBSCRIBE [RFC6665]
  public static final SipMethod SUBSCRIBE = new SipMethod("SUBSCRIBE");
  // UPDATE [RFC3311]
  public static final SipMethod UPDATE = new SipMethod("UPDATE");

  /**
   *
   */
  public static final Map<String, SipMethod> METHOD_MAP = new HashMap<>();

  static {
    METHOD_MAP.put(ACK.method, ACK);
    METHOD_MAP.put(BYE.method, BYE);
    METHOD_MAP.put(CANCEL.method, CANCEL);
    METHOD_MAP.put(INFO.method, INFO);
    METHOD_MAP.put(INVITE.method, INVITE);
    METHOD_MAP.put(MESSAGE.method, MESSAGE);
    METHOD_MAP.put(NOTIFY.method, NOTIFY);
    METHOD_MAP.put(OPTIONS.method, OPTIONS);
    METHOD_MAP.put(PRACK.method, PRACK);
    METHOD_MAP.put(PUBLISH.method, PUBLISH);
    METHOD_MAP.put(REFER.method, REFER);
    METHOD_MAP.put(REGISTER.method, REGISTER);
    METHOD_MAP.put(SUBSCRIBE.method, SUBSCRIBE);
    METHOD_MAP.put(UPDATE.method, UPDATE);
  }

  private final String method;

  /**
   * We keep this private to allow us to use flyweight pattern.
   *
   * @param method
   */

  private SipMethod(final String method) {
    this.method = method.intern();
  }

  /**
   * Returns an object which represents the given method.
   *
   * @param string
   *          The String representation of the method.
   *
   * @return An object which represents the method.
   */

  public static SipMethod fromString(final String string) {

    if (METHOD_MAP.containsKey(string)) {
      return METHOD_MAP.get(string);
    }

    // TODO: perhaps use a guava cache to keep top hits in a cache

    return new SipMethod(string.intern());

  }

  public String getMethod() {
    return this.method;
  }

  @Override
  public boolean equals(final Object other) {
    final SipMethod ometh = SipMethod.class.cast(other);
    return (ometh != null) && ometh.method.equals(this.method);
  }

  @Override
  public int hashCode() {
    return this.method.hashCode();
  }

  @Override
  public String toString() {
    return this.method;
  }

  public static SipMethod of(final CharSequence method) {
    return fromString((String) method);
  }

  public static Function<? super Token, SipMethod> tokenConverter() {
    return input -> SipMethod.fromString(input.toString());
  }

  public boolean isInvite() {
    return this.equals(SipMethod.INVITE);
  }

  public boolean isBye() {
    return this.equals(SipMethod.BYE);
  }

  public boolean isAck() {
    return this.equals(SipMethod.ACK);
  }

  public boolean isCancel() {
    return this.equals(SipMethod.CANCEL);
  }

  public boolean isRefer() {
    return this.equals(SipMethod.REFER);
  }

  public boolean isUpdate() {
    return this.equals(SipMethod.UPDATE);
  }

  public boolean isNotify() {
    return this.equals(SipMethod.NOTIFY);
  }

  public boolean isSubscribe() {
    return this.equals(SipMethod.SUBSCRIBE);
  }

  public boolean isMessage() {
    return this.equals(SipMethod.MESSAGE);
  }

  public boolean isInfo() {
    return this.equals(SipMethod.INFO);
  }

  public boolean isPrack() {
    return this.equals(SipMethod.PRACK);
  }

  public boolean isPublish() {
    return this.equals(SipMethod.PUBLISH);
  }

  public boolean isRegister() {
    return this.equals(SipMethod.REGISTER);
  }

  public boolean isOptions() {
    return this.equals(SipMethod.OPTIONS);
  }
}
