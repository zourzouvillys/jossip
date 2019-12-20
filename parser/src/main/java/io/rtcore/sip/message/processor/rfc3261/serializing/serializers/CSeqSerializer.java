/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import io.rtcore.sip.message.message.api.CSeq;

/**
 * 
 */

public class CSeqSerializer extends AbstractRfcSerializer<CSeq> {

  @Override
  public String serialize(final CSeq obj) {
    return obj.sequence() + " " + obj.method().getMethod();
  }

}
