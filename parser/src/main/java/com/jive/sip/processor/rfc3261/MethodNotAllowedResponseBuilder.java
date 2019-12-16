/**
 * 
 */
package com.jive.sip.processor.rfc3261;

import com.google.common.base.Joiner;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.api.SipResponseStatus;
import com.jive.sip.message.api.TokenSet;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class MethodNotAllowedResponseBuilder extends DefaultResponseBuilder {

  /**
   * @param allowedMethods
   */
  public MethodNotAllowedResponseBuilder(final RfcSipMessageManager manager, final TokenSet allowedMethods) {
    super(manager, SipResponseStatus.METHOD_NOT_ALLOWED);
    final String value = Joiner.on(", ").join(allowedMethods);
    this.addHeader(new RawHeader("Allow", value));
  }

}
