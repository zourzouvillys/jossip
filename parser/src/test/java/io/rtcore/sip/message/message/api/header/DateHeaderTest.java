package io.rtcore.sip.message.message.api.header;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.processor.rfc3261.parsing.SipMessageParseFailureException;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.DateTimeParser;

/**
 * 
 *
 */
public class DateHeaderTest extends BaseParserTest<ZonedDateTime> {

  public DateHeaderTest() {
    super(new DateTimeParser());
  }

  @Test
  public void testDate() throws SipMessageParseFailureException {
    assertEquals(ZonedDateTime.of(2010, 11, 13, 23, 29, 0, 0, ZoneId.of("UTC")),
      this.parse("Sat, 13 Nov 2010 23:29:00 GMT"));
  }

  @Test
  public void testBadDate() throws SipMessageParseFailureException {
    final String test = "Sat, 13 Nov 2010 23:29:00";
    assertEquals(null, this.parse(test, test.length()));
  }
}
