/**
 * 
 */
package com.jive.sip.message.api.header;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

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

    assertEquals(Integer.valueOf(1), timestamp.getTimePartOne());
    assertFalse(timestamp.getTimePartTwo().isPresent());
    assertFalse(timestamp.getDelayPartOne().isPresent());
    assertFalse(timestamp.getDelayPartTwo().isPresent());
  }

  @Test
  public void testJustTime() throws SipMessageParseFailureException {
    final RfcTimestamp timestamp = this.parse("1.0");

    assertEquals(Integer.valueOf(1), timestamp.getTimePartOne());
    assertEquals(Integer.valueOf(0), timestamp.getTimePartTwo().get());
    assertFalse(timestamp.getDelayPartOne().isPresent());
    assertFalse(timestamp.getDelayPartTwo().isPresent());
  }

  @Test
  public void testTimeAndDelayWithTab() throws SipMessageParseFailureException {
    final RfcTimestamp timestamp = this.parse("2.3\t4.2");

    assertEquals(Integer.valueOf(2), timestamp.getTimePartOne());
    assertEquals(Integer.valueOf(3), timestamp.getTimePartTwo().get());
    assertEquals(Integer.valueOf(4), timestamp.getDelayPartOne().get());
    assertEquals(Integer.valueOf(2), timestamp.getDelayPartTwo().get());
  }

  @Test
  public void testTimeAndDelayWithSpace() throws SipMessageParseFailureException {
    final RfcTimestamp timestamp = this.parse("2.3 4.2");

    assertEquals(Integer.valueOf(2), timestamp.getTimePartOne());
    assertEquals(Integer.valueOf(3), timestamp.getTimePartTwo().get());
    assertEquals(Integer.valueOf(4), timestamp.getDelayPartOne().get());
    assertEquals(Integer.valueOf(2), timestamp.getDelayPartTwo().get());
  }

  @Test
  public void testTimeAndDelayWithMixedWhiteSpace() throws SipMessageParseFailureException {
    final RfcTimestamp timestamp = this.parse("2 \t \t4.2");

    assertEquals(Integer.valueOf(2), timestamp.getTimePartOne());
    assertFalse(timestamp.getTimePartTwo().isPresent());
    assertEquals(Integer.valueOf(4), timestamp.getDelayPartOne().get());
    assertEquals(Integer.valueOf(2), timestamp.getDelayPartTwo().get());
  }

  @Test
  public void testFullTimeWithSimpleDelay() throws SipMessageParseFailureException {
    final RfcTimestamp timestamp = this.parse("2.3 4");

    assertEquals(Integer.valueOf(2), timestamp.getTimePartOne());
    assertEquals(Integer.valueOf(3), timestamp.getTimePartTwo().get());
    assertEquals(Integer.valueOf(4), timestamp.getDelayPartOne().get());
    assertFalse(timestamp.getDelayPartTwo().isPresent());
  }

  @Test
  public void testFullTimeWithPartialDelay() throws SipMessageParseFailureException {
    final RfcTimestamp timestamp = this.parse("2.3 4.");

    assertEquals(Integer.valueOf(2), timestamp.getTimePartOne());
    assertEquals(Integer.valueOf(3), timestamp.getTimePartTwo().get());
    assertEquals(Integer.valueOf(4), timestamp.getDelayPartOne().get());
    assertFalse(timestamp.getDelayPartTwo().isPresent());
  }

  @Test
  public void testTimeDigitAndDot() throws SipMessageParseFailureException {
    final RfcTimestamp timestamp = this.parse("2. 1.");

    assertEquals(Integer.valueOf(2), timestamp.getTimePartOne());
    assertFalse(timestamp.getTimePartTwo().isPresent());
    assertEquals(Integer.valueOf(1), timestamp.getDelayPartOne().get());
    assertFalse(timestamp.getDelayPartTwo().isPresent());

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
