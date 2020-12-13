package io.rtcore.sip.bind;

public class SipProcessingException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  protected final SipLocation _location;

  protected SipProcessingException(String msg, SipLocation loc, Throwable rootCause) {
    super(msg);
    if (rootCause != null) {
      initCause(rootCause);
    }
    _location = loc;
  }

  protected SipProcessingException(String msg) {
    super(msg);
    this._location = null;
  }

  protected SipProcessingException(String msg, SipLocation loc) {
    this(msg, loc, null);
  }

  protected SipProcessingException(String msg, Throwable rootCause) {
    this(msg, null, rootCause);
  }

  protected SipProcessingException(Throwable rootCause) {
    this(null, null, rootCause);
  }

  public SipLocation getLocation() {
    return _location;
  }

  /**
   * Method that allows accessing the original "message" argument, without additional decorations
   * (like location information) that overridden {@link #getMessage} adds.
   */

  public String getOriginalMessage() {
    return super.getMessage();
  }

  /**
   * Method that allows accessing underlying processor that triggered this exception; typically
   * either {@link SipParser} or {@link SipGenerator} for exceptions that originate from streaming
   * API. Note that it is possible that `null` may be returned if code throwing exception either has
   * no access to processor; or has not been retrofitted to set it; this means that caller needs to
   * take care to check for nulls. Subtypes override this method with co-variant return type, for
   * more type-safe access.
   * 
   * @return Originating processor, if available; null if not.
   */

  public Object getProcessor() {
    return null;
  }

  /**
   * Accessor that sub-classes can override to append additional information right after the main
   * message, but before source location information.
   */
  protected String getMessageSuffix() {
    return null;
  }

  /**
   * Default method overridden so that we can add location information
   */

  @Override
  public String getMessage() {
    String msg = super.getMessage();
    if (msg == null) {
      msg = "N/A";
    }
    SipLocation loc = getLocation();
    String suffix = getMessageSuffix();
    // mild optimization, if nothing extra is needed:
    if ((loc != null) || (suffix != null)) {
      StringBuilder sb = new StringBuilder(100);
      sb.append(msg);
      if (suffix != null) {
        sb.append(suffix);
      }
      if (loc != null) {
        sb.append('\n');
        sb.append(" at ");
        sb.append(loc.toString());
      }
      msg = sb.toString();
    }
    return msg;
  }

  @Override
  public String toString() {
    return getClass().getName() + ": " + getMessage();
  }

}
