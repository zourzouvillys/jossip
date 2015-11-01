/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.jive.sip.base.api.RawHeader;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class RawHeaderSerializer extends AbstractRfcSerializer<RawHeader>
{

  /*
   * (non-Javadoc)
   * 
   * @see com.jive.sip.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */
  @Override
  public void serialize(final Writer w, final RawHeader obj) throws IOException
  {

    w.append(obj.getName());

    w.append(RfcSerializationConstants.COLON);
    w.append(RfcSerializationConstants.SP);

    if (obj.getValue() != null)
    {
      w.append(obj.getValue());
    }

  }

}
