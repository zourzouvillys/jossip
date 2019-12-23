package io.rtcore.sip.verifiers;

import com.google.common.base.VerifyException;

/**
 * indicates an error verifying semantic or syntactic properties within a SIP message.
 * 
 * The message will always be safe to provide externally.
 * 
 */

public class SipVerifyException extends VerifyException {

  private static final long serialVersionUID = 1L;

  public SipVerifyException() {
  }

  public SipVerifyException(String message) {
    super(message);
  }

  public SipVerifyException(Throwable cause) {
    super(cause);
  }

  public SipVerifyException(String message, Throwable cause) {
    super(message, cause);
  }

}
