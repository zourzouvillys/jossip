/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.jive.sip.message.api.SipResponse;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager;

import lombok.AllArgsConstructor;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
@AllArgsConstructor
public class SipResponseSerializer extends AbstractRfcSerializer<SipResponse>
{

  private final RfcSerializerManager manager;

  /*
   * (non-Javadoc)
   * 
   * @see com.jive.sip.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */
  @Override
  public void serialize(final Writer writer, final SipResponse obj) throws IOException
  {

    writer.append(obj.getVersion());
    writer.append(RfcSerializationConstants.SP);
    writer.append(Integer.toString(obj.getStatus().getCode()));
    writer.append(RfcSerializationConstants.SP);
    writer.append(obj.getStatus().getReason());
    writer.append(RfcSerializationConstants.CRLF);

    this.manager.serializeCollection(writer, obj.getHeaders(), RfcSerializationConstants.CRLF);

    writer.append(RfcSerializationConstants.CRLF);
    writer.append(RfcSerializationConstants.CRLF);

    if (obj.getBody() != null)
    {
      // TODO: urgh, perhaps not Writer?
      writer.write(new String(obj.getBody()).toCharArray());
    }

  }

}
