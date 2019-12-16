/**
 * 
 */
package com.jive.sip.message.api.header;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.jive.sip.message.api.headers.RfcTimestamp;
import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.processor.rfc3261.parsing.SipMessageParseFailureException;
import com.jive.sip.processor.rfc3261.parsing.parsers.headers.RfcTimestampParser;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class RfcTimestampTest extends BaseParserTest<RfcTimestamp> {

  public RfcTimestampTest() {
    super(new RfcTimestampParser());
  }

  @Test
  public void testSimpleTime() throws SipMessageParseFailureException {
    final RfcTimestamp timestamp = this.parse("1");

    assertEquals(Integer.valueOf(1), timestamp.timePartOne());
    assertFalse(timestamp.timePartTwo().isPresent());
    assertFalse(timestamp.delayPartOne().isPresent());
    assertFalse(timestamp.delayPartTwo().isPresent());
  }

  @Test
  public void testJustTime() throws SipMessageParseFailureException {
    final RfcTimestamp timestamp = this.parse("1.0");

    assertEquals(Integer.valueOf(1), timestamp.timePartOne());
    assertEquals(Integer.valueOf(0), timestamp.timePartTwo().get());
    assertFalse(timestamp.delayPartOne().isPresent());
    assertFalse(timestamp.delayPartTwo().isPresent());
  }

  @Test
  public void testTimeAndDelayWithTab() throws SipMessageParseFailureException {
    final RfcTimestamp timestamp = this.parse("2.3\t4.2");

    assertEquals(Integer.valueOf(2), timestamp.timePartOne());
    assertEquals(Integer.valueOf(3), timestamp.timePartTwo().get());
    assertEquals(Integer.valueOf(4), timestamp.delayPartOne().get());
    assertEquals(Integer.valueOf(2), timestamp.delayPartTwo().get());
  }

  @Test
  public void testTimeAndDelayWithSpace() throws SipMessageParseFailureException {
    final RfcTimestamp timestamp = this.parse("2.3 4.2");

    assertEquals(Integer.valueOf(2), timestamp.timePartOne());
    assertEquals(Integer.valueOf(3), timestamp.timePartTwo().get());
    assertEquals(Integer.valueOf(4), timestamp.delayPartOne().get());
    assertEquals(Integer.valueOf(2), timestamp.delayPartTwo().get());
  }

  @Test
  public void testTimeAndDelayWithMixedWhiteSpace() throws SipMessageParseFailureException {
    final RfcTimestamp timestamp = this.parse("2 \t \t4.2");

    assertEquals(Integer.valueOf(2), timestamp.timePartOne());
    assertFalse(timestamp.timePartTwo().isPresent());
    assertEquals(Integer.valueOf(4), timestamp.delayPartOne().get());
    assertEquals(Integer.valueOf(2), timestamp.delayPartTwo().get());
  }

  @Test
  public void testFullTimeWithSimpleDelay() throws SipMessageParseFailureException {
    final RfcTimestamp timestamp = this.parse("2.3 4");

    assertEquals(Integer.valueOf(2), timestamp.timePartOne());
    assertEquals(Integer.valueOf(3), timestamp.timePartTwo().get());
    assertEquals(Integer.valueOf(4), timestamp.delayPartOne().get());
    assertFalse(timestamp.delayPartTwo().isPresent());
  }

  @Test
  public void testFullTimeWithPartialDelay() throws SipMessageParseFailureException {
    final RfcTimestamp timestamp = this.parse("2.3 4.");

    assertEquals(Integer.valueOf(2), timestamp.timePartOne());
    assertEquals(Integer.valueOf(3), timestamp.timePartTwo().get());
    assertEquals(Integer.valueOf(4), timestamp.delayPartOne().get());
    assertFalse(timestamp.delayPartTwo().isPresent());
  }

  @Test
  public void testTimeDigitAndDot() throws SipMessageParseFailureException {
    final RfcTimestamp timestamp = this.parse("2. 1.");

    assertEquals(Integer.valueOf(2), timestamp.timePartOne());
    assertFalse(timestamp.timePartTwo().isPresent());
    assertEquals(Integer.valueOf(1), timestamp.delayPartOne().get());
    assertFalse(timestamp.delayPartTwo().isPresent());

  }

  @Test
  public void testBadTimeFormat() throws SipMessageParseFailureException {
    this.parse("4.g", 1);
  }

  @Test
  public void testTooManyParameters() throws SipMessageParseFailureException {
    this.parse("4. 2. 3.", 3);
  }
}
