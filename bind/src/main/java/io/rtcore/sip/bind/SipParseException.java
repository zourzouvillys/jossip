package io.rtcore.sip.bind;

public class SipParseException extends StreamReadException {

  private static final long serialVersionUID = 1L;

  protected SipParseException(String msg) {
    this(msg, null, null);
  }

  protected SipParseException(String msg, SipLocation loc, Throwable rootCause) {
    super(msg, loc, rootCause);
  }

}
