package io.rtcore.sip.netty.codec;


import static io.netty.util.ByteProcessor.FIND_ASCII_SPACE;
import static io.rtcore.sip.netty.codec.SipConstants.SP;
import static java.lang.Integer.parseInt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

public class SipResponseStatus implements Comparable<SipResponseStatus> {

  /**
   * 
   */

  public static final SipResponseStatus TRYING = newStatus(100, "Trying");
  public static final SipResponseStatus PROVISIONAL = newStatus(180, "Provisional");
  public static final SipResponseStatus RINGING = newStatus(183, "Ringing");
  public static final SipResponseStatus OK = newStatus(200, "OK");
  public static final SipResponseStatus NOT_FOUND = newStatus(404, "Not Found");
  public static final SipResponseStatus SERVER_INTERNAL_ERROR = newStatus(500, "Server Internal Error");

  /**
   * 
   * @param code
   * @return
   */

  private static SipResponseStatus valueOf0(int code) {
    switch (code) {
      case 100:
        return TRYING;
      case 180:
        return PROVISIONAL;
      case 183:
        return RINGING;
      case 200:
        return OK;
      case 404:
        return NOT_FOUND;
      case 500:
        return SERVER_INTERNAL_ERROR;
    }
    return null;
  }

  /**
   * 
   * @param statusCode
   * @param reasonPhrase
   * @return
   */

  private static SipResponseStatus newStatus(int statusCode, String reasonPhrase) {
    return new SipResponseStatus(statusCode, reasonPhrase, true);
  }

  /**
   * Returns the {@link SipResponseStatus} represented by the specified code. If the specified code
   * is a standard SIP status code, a cached instance will be returned. Otherwise, a new instance
   * will be returned.
   */

  public static SipResponseStatus valueOf(int code) {
    SipResponseStatus status = valueOf0(code);
    return status != null ? status
                          : new SipResponseStatus(code);
  }

  /**
   * Returns the {@link SipResponseStatus} represented by the specified {@code code} and
   * {@code reasonPhrase}. If the specified code is a standard SIP status {@code code} and
   * {@code reasonPhrase}, a cached instance will be returned. Otherwise, a new instance will be
   * returned.
   * 
   * @param code
   *          The response code value.
   * @param reasonPhrase
   *          The response code reason phrase.
   * @return the {@link SipResponseStatus} represented by the specified {@code code} and
   *         {@code reasonPhrase}.
   */

  public static SipResponseStatus valueOf(int code, String reasonPhrase) {
    SipResponseStatus responseStatus = valueOf0(code);
    return (responseStatus != null)
      && responseStatus.reasonPhrase()
        .contentEquals(reasonPhrase) ? responseStatus
                                     : new SipResponseStatus(code, reasonPhrase);
  }

  /**
   * Parses the specified SIP status line into a {@link SipResponseStatus}. The expected formats of
   * the line are:
   * <ul>
   * <li>{@code statusCode} (e.g. 200)</li>
   * <li>{@code statusCode} {@code reasonPhrase} (e.g. 404 Not Found)</li>
   * </ul>
   *
   * @throws IllegalArgumentException
   *           if the specified status line is malformed
   */
  public static SipResponseStatus parseLine(CharSequence line) {
    return (line instanceof AsciiString) ? parseLine((AsciiString) line)
                                         : parseLine(line.toString());
  }

  /**
   * Parses the specified SIP status line into a {@link SipResponseStatus}. The expected formats of
   * the line are:
   * <ul>
   * <li>{@code statusCode} (e.g. 200)</li>
   * <li>{@code statusCode} {@code reasonPhrase} (e.g. 404 Not Found)</li>
   * </ul>
   *
   * @throws IllegalArgumentException
   *           if the specified status line is malformed
   */
  public static SipResponseStatus parseLine(String line) {
    try {
      int space = line.indexOf(' ');
      return space == -1 ? valueOf(parseInt(line))
                         : valueOf(parseInt(line.substring(0, space)), line.substring(space + 1));
    }
    catch (Exception e) {
      throw new IllegalArgumentException("malformed status line: " + line, e);
    }
  }

  /**
   * Parses the specified SIP status line into a {@link SipResponseStatus}. The expected formats of
   * the line are:
   * <ul>
   * <li>{@code statusCode} (e.g. 200)</li>
   * <li>{@code statusCode} {@code reasonPhrase} (e.g. 404 Not Found)</li>
   * </ul>
   *
   * @throws IllegalArgumentException
   *           if the specified status line is malformed
   */
  public static SipResponseStatus parseLine(AsciiString line) {
    try {
      int space = line.forEachByte(FIND_ASCII_SPACE);
      return space == -1 ? valueOf(line.parseInt())
                         : valueOf(line.parseInt(0, space), line.toString(space + 1));
    }
    catch (Exception e) {
      throw new IllegalArgumentException("malformed status line: " + line, e);
    }
  }

  private final int code;
  private final AsciiString codeAsText;
  private SipStatusClass codeClass;

  private final String reasonPhrase;
  private final byte[] bytes;

  /**
   * Creates a new instance with the specified {@code code} and the auto-generated default reason
   * phrase.
   */
  private SipResponseStatus(int code) {
    this(code, SipStatusClass.valueOf(code).defaultReasonPhrase() + " (" + code + ')', false);
  }

  /**
   * Creates a new instance with the specified {@code code} and its {@code reasonPhrase}.
   */
  public SipResponseStatus(int code, String reasonPhrase) {
    this(code, reasonPhrase, false);
  }

  private SipResponseStatus(int code, String reasonPhrase, boolean bytes) {
    if (code < 0) {
      throw new IllegalArgumentException(
        "code: " + code + " (expected: 0+)");
    }

    if (reasonPhrase == null) {
      throw new NullPointerException("reasonPhrase");
    }

    for (int i = 0; i < reasonPhrase.length(); i++) {
      char c = reasonPhrase.charAt(i);
      // Check prohibited characters.
      switch (c) {
        case '\n':
        case '\r':
          throw new IllegalArgumentException(
            "reasonPhrase contains one of the following prohibited characters: "
              +
              "\\r\\n: "
              + reasonPhrase);
      }
    }

    this.code = code;
    String codeString = Integer.toString(code);
    codeAsText = new AsciiString(codeString);
    this.reasonPhrase = reasonPhrase;
    if (bytes) {
      this.bytes = (codeString + ' ' + reasonPhrase).getBytes(CharsetUtil.US_ASCII);
    }
    else {
      this.bytes = null;
    }
  }

  /**
   * Returns the code of this {@link SipResponseStatus}.
   */
  public int code() {
    return code;
  }

  /**
   * Returns the status code as {@link AsciiString}.
   */
  public AsciiString codeAsText() {
    return codeAsText;
  }

  /**
   * Returns the reason phrase of this {@link SipResponseStatus}.
   */
  public String reasonPhrase() {
    return reasonPhrase;
  }

  /**
   * Returns the class of this {@link SipResponseStatus}
   */
  public SipStatusClass codeClass() {
    SipStatusClass type = this.codeClass;
    if (type == null) {
      this.codeClass = type = SipStatusClass.valueOf(code);
    }
    return type;
  }

  @Override
  public int hashCode() {
    return code();
  }

  /**
   * Equality of {@link SipResponseStatus} only depends on {@link #code()}. The reason phrase is not
   * considered for equality.
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SipResponseStatus)) {
      return false;
    }

    return code() == ((SipResponseStatus) o).code();
  }

  /**
   * Equality of {@link SipResponseStatus} only depends on {@link #code()}. The reason phrase is not
   * considered for equality.
   */
  @Override
  public int compareTo(SipResponseStatus o) {
    return code() - o.code();
  }

  @Override
  public String toString() {
    return new StringBuilder(reasonPhrase.length() + 4)
      .append(codeAsText)
      .append(' ')
      .append(reasonPhrase)
      .toString();
  }

  public void encode(ByteBuf buf) {
    if (bytes == null) {
      ByteBufUtil.copy(codeAsText, buf);
      buf.writeByte(SP);
      buf.writeCharSequence(reasonPhrase, CharsetUtil.US_ASCII);
    }
    else {
      buf.writeBytes(bytes);
    }
  }
}
