/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.serializing;

import java.io.IOException;
import java.io.Writer;

/**
 * 
 * 
 */
public interface RfcSerializer<T> {

  /**
   * Serializes the given object to the provided {@link Writer}.
   * 
   * @param sink
   *          The sink to write to
   * @param obj
   *          The object to serialize
   * @throws IOException
   *           If the sink throws an {@link IOException}.
   */

  void serialize(final Writer sink, final T obj) throws IOException;

  String serialize(final T obj);

}
