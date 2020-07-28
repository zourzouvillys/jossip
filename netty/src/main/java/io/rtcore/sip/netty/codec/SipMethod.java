package io.rtcore.sip.netty.codec;


import static io.netty.util.internal.ObjectUtil.checkNotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;

public class SipMethod implements Comparable<SipMethod> {

  public static final SipMethod INVITE = new SipMethod("INVITE");
  public static final SipMethod ACK = new SipMethod("ACK");
  public static final SipMethod BYE = new SipMethod("BYE");
  public static final SipMethod CANCEL = new SipMethod("CANCEL");
  public static final SipMethod REGISTER = new SipMethod("REGISTER");
  public static final SipMethod OPTIONS = new SipMethod("OPTIONS");
  public static final SipMethod PRACK = new SipMethod("PRACK");
  public static final SipMethod SUBSCRIBE = new SipMethod("SUBSCRIBE");
  public static final SipMethod NOTIFY = new SipMethod("NOTIFY");
  public static final SipMethod PUBLISH = new SipMethod("PUBLISH");
  public static final SipMethod INFO = new SipMethod("INFO");
  public static final SipMethod REFER = new SipMethod("REFER");
  public static final SipMethod MESSAGE = new SipMethod("MESSAGE");
  public static final SipMethod UPDATE = new SipMethod("UPDATE");

  private static SipMethod[] allMethods =
    {
      INVITE,
      ACK,
      BYE,
      CANCEL,
      REGISTER,
      OPTIONS,
      PRACK,
      SUBSCRIBE,
      NOTIFY,
      PUBLISH,
      INFO,
      REFER,
      MESSAGE,
      UPDATE
    };

  private AsciiString name;

  private SipMethod(String name) {

    name = checkNotNull(name, "name").trim();

    if (name.isEmpty()) {
      throw new IllegalArgumentException("empty name");
    }

    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (Character.isISOControl(c) || Character.isWhitespace(c)) {
        throw new IllegalArgumentException("invalid character in name");
      }
    }

    this.name = AsciiString.cached(name);

  }

  private static final Map<String, SipMethod> methodMap;

  static {
    methodMap =
      Arrays.stream(allMethods)
        .collect(Collectors.toMap(e -> e.toString(), Function.identity()));
  }

  /**
   * Returns the {@link SipMethod} represented by the specified name. If the specified name is a
   * standard SIP method name, a cached instance will be returned. Otherwise, a new instance will be
   * returned.
   */

  public static SipMethod valueOf(String name) {
    SipMethod result = methodMap.get(name);
    return result != null ? result
                          : new SipMethod(name);
  }

  /**
   * Returns the name of this method.
   */
  public String name() {
    return name.toString();
  }

  /**
   * Returns the name of this method.
   */
  public AsciiString asciiName() {
    return name;
  }

  @Override
  public int hashCode() {
    return name().hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SipMethod)) {
      return false;
    }
    SipMethod that = (SipMethod) o;
    return name().equals(that.name());
  }

  @Override
  public String toString() {
    return name.toString();
  }

  @Override
  public int compareTo(SipMethod o) {
    return name().compareTo(o.name());
  }

  public void encode(ByteBuf buf) {
    buf.writeBytes(this.asciiName().array());
  }

}
