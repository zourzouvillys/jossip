/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;
import java.net.URLEncoder;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;
import io.rtcore.sip.message.uri.SipUri;

/**
 * 
 * 
 */

public class SipUriSerializer extends AbstractRfcSerializer<SipUri> {

  private final RfcSerializerManager manager;

  public SipUriSerializer(final RfcSerializerManager manager) {
    this.manager = manager;
  }

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */

  @Override
  public void serialize(final Writer sb, final SipUri obj) throws IOException {

    sb.append(obj.getScheme()).append(':');

    if (obj.getUserinfo().isPresent()) {
      this.manager.serialize(sb, obj.getUserinfo().get());
      sb.append('@');
    }

    sb.append(obj.getHost().toString());

    if (obj.getParameters().isPresent()) {
      sb.append(RfcSerializationConstants.SEMI);
      this.manager.serializeCollection(sb, obj.getParameters().get().getRawParameters(), RfcSerializationConstants.SEMI);
    }

    if ((obj.getHeaders() != null) && !obj.getHeaders().isEmpty()) {

      final StringBuilder hb = new StringBuilder();

      for (final RawHeader header : obj.getHeaders()) {
        if (hb.length() > 0) {
          hb.append('&');
        }
        hb.append(header.name()).append('=').append(URLEncoder.encode(header.value(), "UTF-8"));
      }

      sb.append('?').append(hb.toString());

    }

  }

}
