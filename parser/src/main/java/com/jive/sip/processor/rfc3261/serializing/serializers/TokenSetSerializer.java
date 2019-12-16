/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.jive.sip.base.api.Token;
import com.jive.sip.message.api.TokenSet;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class TokenSetSerializer extends AbstractRfcSerializer<TokenSet> {

  /*
   * (non-Javadoc)
   * @see com.jive.sip.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */

  @Override
  public void serialize(final Writer w, final TokenSet obj) throws IOException {
    int i = 0;
    for (final Token tok : obj) {
      if (i++ > 0) {
        w.append(", ");
      }
      w.append(tok.toString());
    }
  }
}
