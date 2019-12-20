/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261;

import com.google.common.base.Joiner;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.message.SipResponseStatus;
import io.rtcore.sip.message.message.api.TokenSet;

/**
 * 
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
