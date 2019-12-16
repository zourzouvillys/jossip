package com.jive.sip.processor.rfc3261.parsing;

import com.jive.sip.processor.rfc3261.HeaderParseContext;

/**
 * Exception to indicate a failure in parsing.
 * 
 * Checked, as it's part of the contract of the APIs that throw this.
 * 
 * @author theo
 * 
 */

public class SipMessageParseFailureException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public SipMessageParseFailureException(final String msg) {
    super(msg);
  }

  public SipMessageParseFailureException(final String msg, final HeaderParseContext ctx, final int offset) {
    super(msg + ", got: '" + ctx.subSequence(offset, ctx.length() - offset) + "'");
  }

}
