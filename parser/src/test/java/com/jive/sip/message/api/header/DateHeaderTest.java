/**
 *
 */
package com.jive.sip.message.api.header;

import static org.junit.Assert.assertEquals;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.processor.rfc3261.parsing.SipMessageParseFailureException;
import com.jive.sip.processor.rfc3261.parsing.parsers.headers.DateTimeParser;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
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
