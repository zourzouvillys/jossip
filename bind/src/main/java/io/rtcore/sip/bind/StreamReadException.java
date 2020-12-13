package io.rtcore.sip.bind;

public class StreamReadException extends SipProcessingException {

  private static final long serialVersionUID = 1L;

  protected StreamReadException(String msg, SipLocation loc, Throwable rootCause) {
    super(msg, loc);
    if (rootCause != null) {
      initCause(rootCause);
    }
  }

}
