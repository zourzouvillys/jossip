package io.rtcore.sip.netty.codec;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

public class SipVersion implements Comparable<SipVersion> {

  public static final SipVersion SIP_2_0 = new SipVersion("SIP", 2, 0);
  
  private String protocolName;
  private int majorVersion;
  private int minorVersion;

  private String text;

  public SipVersion(String protocolName, int majorVersion, int minorVersion) {
    this.protocolName = protocolName;
    this.text = protocolName + '/' + majorVersion + '.' + minorVersion;

  }

  public void encode(ByteBuf buf) {
    buf.writeCharSequence(text, CharsetUtil.US_ASCII);
  }

  public static SipVersion valueOf(String string) {
    if (string.contentEquals(SIP_2_0.text)) {
      return SIP_2_0;
    }
    //

    return new SipVersion(string);
  }

  private static final Pattern VERSION_PATTERN =
    Pattern.compile("(\\S+)/(\\d+)\\.(\\d+)");

  public SipVersion(String text) {
    if (text == null) {
      throw new NullPointerException("text");
    }

    text = text.trim().toUpperCase();
    if (text.isEmpty()) {
      throw new IllegalArgumentException("empty text");
    }

    Matcher m = VERSION_PATTERN.matcher(text);
    if (!m.matches()) {
      throw new IllegalArgumentException("invalid version format: " + text);
    }

    protocolName = m.group(1);
    majorVersion = Integer.parseInt(m.group(2));
    minorVersion = Integer.parseInt(m.group(3));
    this.text = protocolName + '/' + majorVersion + '.' + minorVersion;
  }

  /**
   * Returns the name of the protocol such as {@code "SIP"} in {@code "SIP/2.0"}.
   */
  public String protocolName() {
    return protocolName;
  }

  /**
   * Returns the name of the protocol such as {@code 2} in {@code "SIP/2.0"}.
   */
  public int majorVersion() {
    return majorVersion;
  }

  /**
   * Returns the name of the protocol such as {@code 0} in {@code "SIP/2.0"}.
   */
  public int minorVersion() {
    return minorVersion;
  }

  /**
   * Returns the full protocol version text such as {@code "SIP/2.0"}.
   */
  public String text() {
    return text;
  }

  /**
   * Returns the full protocol version text such as {@code "SIP/2.0"}.
   */
  @Override
  public String toString() {
    return text();
  }

  @Override
  public int hashCode() {
    return (((protocolName().hashCode() * 31) + majorVersion()) * 31)
      +
      minorVersion();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SipVersion)) {
      return false;
    }
    SipVersion that = (SipVersion) o;
    return (minorVersion() == that.minorVersion())
      &&
      (majorVersion() == that.majorVersion())
      &&
      protocolName().equals(that.protocolName());
  }

  @Override
  public int compareTo(SipVersion o) {
    int v = protocolName().compareTo(o.protocolName());
    if (v != 0) {
      return v;
    }

    v = majorVersion() - o.majorVersion();
    if (v != 0) {
      return v;
    }

    return minorVersion() - o.minorVersion();
  }

}
