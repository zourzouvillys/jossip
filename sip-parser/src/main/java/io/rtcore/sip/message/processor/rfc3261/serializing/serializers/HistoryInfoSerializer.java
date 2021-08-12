/**
 *
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import io.rtcore.sip.message.message.api.headers.HistoryInfo;
import io.rtcore.sip.message.message.api.headers.HistoryInfo.Entry;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;

/**
 * 
 *
 */
public class HistoryInfoSerializer extends AbstractRfcSerializer<HistoryInfo> {

  private final RfcSerializerManager manager;

  public HistoryInfoSerializer(final RfcSerializerManager manager) {
    this.manager = manager;
  }

  @Override
  public void serialize(final Writer sink, final HistoryInfo obj) throws IOException {

    int i = 0;

    for (final Entry e : obj.entries()) {
      if (i++ > 0) {
        sink.append(", ");
      }
      this.manager.serialize(sink, e.toNameAddr());
    }

  }

}
