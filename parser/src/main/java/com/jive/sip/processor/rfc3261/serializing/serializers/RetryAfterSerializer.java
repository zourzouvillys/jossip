/**
 *
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import com.jive.sip.message.api.headers.RetryAfter;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */
public class RetryAfterSerializer extends AbstractRfcSerializer<RetryAfter>
{

  private final RfcSerializerManager manager;

  public RetryAfterSerializer(final RfcSerializerManager manager)
  {
    this.manager = manager;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.jive.sip.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(final RetryAfter obj)
  {
    String result = obj.getDelta() + " " + obj.getComment().orElse("");
    result += this.manager.serialize(obj.getParameters());
    return result;
  }

}
