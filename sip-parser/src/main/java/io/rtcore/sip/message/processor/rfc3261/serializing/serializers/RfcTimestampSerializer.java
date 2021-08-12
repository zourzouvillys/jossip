/**
 *
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import io.rtcore.sip.message.message.api.headers.RfcTimestamp;

/**
 * 
 *
 */
public class RfcTimestampSerializer extends AbstractRfcSerializer<RfcTimestamp> {

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(final RfcTimestamp obj) {
    String result = obj.timePartOne() + "." + obj.timePartTwo().orElse(0);
    if (obj.delayPartOne().isPresent() || obj.delayPartTwo().isPresent()) {
      result += " " + obj.delayPartOne().orElse(0) + "." + obj.delayPartTwo().orElse(0);
    }
    return result;
  }

}
