/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261;

import java.util.List;

import com.google.common.base.Joiner;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.message.SipResponseStatus;
import io.rtcore.sip.message.message.api.TokenSet;

/**
 * 
 * 
 */
public class BadExtensionResponseBuilder extends DefaultResponseBuilder {

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
