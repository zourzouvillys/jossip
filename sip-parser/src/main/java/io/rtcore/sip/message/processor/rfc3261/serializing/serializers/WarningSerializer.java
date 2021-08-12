/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import com.google.common.base.Joiner;

import io.rtcore.sip.message.message.api.headers.Warning;

/**
 * 
 * 
 */
public class WarningSerializer extends AbstractRfcSerializer<Warning> {

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(final Warning obj) {
    return Joiner.on(" ").join(obj.code(), obj.agent(), "\"" + obj.text().toString().replace("\"", "\\\"") + "\"");
  }

}
