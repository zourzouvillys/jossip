/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.google.common.primitives.UnsignedInteger;

/**
 * @author Jeff Poole <jpoole@getjive.com>
 * 
 */
public class UnsignedIntegerSerializer extends AbstractRfcSerializer<UnsignedInteger>
{

  /*
   * (non-Javadoc)
   * 
   * @see com.jive.sip.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */

  @Override
  public void serialize(final Writer w, final UnsignedInteger obj) throws IOException
  {
    w.append(obj.toString());
  }
}
