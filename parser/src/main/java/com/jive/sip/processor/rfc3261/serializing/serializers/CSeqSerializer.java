/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import com.jive.sip.message.api.CSeq;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 */

public class CSeqSerializer extends AbstractRfcSerializer<CSeq> {

  @Override
  public String serialize(final CSeq obj) {
    return obj.sequence() + " " + obj.method().getMethod();
  }

}
