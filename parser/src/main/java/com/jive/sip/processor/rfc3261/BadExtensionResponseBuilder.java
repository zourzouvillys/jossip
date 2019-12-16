/**
 * 
 */
package com.jive.sip.processor.rfc3261;

import java.util.List;

import com.google.common.base.Joiner;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.SipResponseStatus;
import com.jive.sip.message.api.TokenSet;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class BadExtensionResponseBuilder extends DefaultResponseBuilder {

  /**
   * @param status
   */

  public BadExtensionResponseBuilder(final TokenSet tokens) {
    super(SipResponseStatus.BAD_EXTENSION);
    final String value = Joiner.on(", ").join(tokens);
    this.addHeader(new RawHeader("Unsupported", value));
  }

  public BadExtensionResponseBuilder(final RfcSipMessageManager manager, final List<String> unsupportedExtensions) {
    super(manager, SipResponseStatus.BAD_EXTENSION);
    final String value = Joiner.on(", ").join(unsupportedExtensions);
    this.addHeader(new RawHeader("Unsupported", value));
  }

}
