/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import com.google.common.base.Joiner;
import com.jive.sip.message.api.headers.Warning;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class WarningSerializer extends AbstractRfcSerializer<Warning> {

  /*
   * (non-Javadoc)
   * @see com.jive.sip.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(final Warning obj) {
    return Joiner.on(" ").join(obj.getCode(), obj.getAgent(), "\"" + obj.getText().toString().replace("\"", "\\\"") + "\"");
  }

}
