/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.message.api.TokenSet;

/**
 * 
 * 
 */
public class TokenSetSerializer extends AbstractRfcSerializer<TokenSet> {

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
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
