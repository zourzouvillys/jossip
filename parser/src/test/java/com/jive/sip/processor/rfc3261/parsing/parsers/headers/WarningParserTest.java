/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.jive.sip.message.api.headers.Warning;
import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.parsers.core.ParseFailureException;
import com.jive.sip.processor.rfc3261.parsing.SipMessageParseFailureException;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class WarningParserTest extends BaseParserTest<Warning> {

  public WarningParserTest() {
    super(new WarningParser());
  }

  @Test
  public void testWarning() throws SipMessageParseFailureException {
    final Warning header = this.parse("307 isi.edu \"Session parameter 'foo' not understood\"");
    assertEquals(307, header.code());
    assertEquals("isi.edu", header.agent());
    assertEquals("Session parameter 'foo' not understood", header.text());
  }

  @Test
  public void testTestNotQuoted() throws ParseFailureException {
    assertNull(this.parse("307 isi.edu Session parameter 'foo' not understood", 50));
  }

  @Test
  public void testCodeNotNumber() throws ParseFailureException {
    assertNull(this.parse("Bad isi.ed \"Message\"", 20));
  }

  @Test
  public void testCodeTooShort() throws ParseFailureException {
    assertNull(this.parse("20 isi.ed \"Message\"", 19));
  }

  @Test
  public void testCodeTooLong() throws ParseFailureException {
    assertNull(this.parse("2000 isi.ed \"Message\"", 21));
  }
}
