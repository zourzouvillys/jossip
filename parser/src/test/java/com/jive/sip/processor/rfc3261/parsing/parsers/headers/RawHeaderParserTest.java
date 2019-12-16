/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jive.sip.base.api.RawHeader;
import com.jive.sip.parsers.core.BaseParserTest;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */
public class RawHeaderParserTest extends BaseParserTest<RawHeader> {

  /**
   * @param parser
   */
  public RawHeaderParserTest() {
    super(new RawHeaderParser());
  }

  @Test
  public void test() {
    assertEquals(new RawHeader("Accept", "application/sdp;level=1, application/x-private, text/html"),
      this.parse("Accept: application/sdp;level=1, application/x-private, text/html\n"));
    assertEquals(new RawHeader("Accept-Encoding", "gzip"), this.parse("Accept-Encoding: gzip\n"));
    assertEquals(new RawHeader("Alert-Info", "<http://www.example.com/sounds/moo.wav>"),
      this.parse("Alert-Info: <http://www.example.com/sounds/moo.wav>\n"));
    assertEquals(new RawHeader("Authentication-Info", "nextnonce=\"47364c23432d2e131a5fb210812c\""),
      this.parse("Authentication-Info: nextnonce=\"47364c23432d2e131a5fb210812c\"\n"));
    assertEquals(new RawHeader("Call-ID", "f81d4fae-7dec-11d0-a765-00a0c91e6bf6@biloxi.com"),
      this.parse("Call-ID: f81d4fae-7dec-11d0-a765-00a0c91e6bf6@biloxi.com\n"));
    assertEquals(new RawHeader(
      "Contact",
      "\"Mr. Watson\" <sip:watson@worcester.bell-telephone.com>\r\n"
        + " ;q=0.7; expires=3600,\r\n"
        + " \"Mr. Watson\" <mailto:watson@bell-telephone.com> ;q=0.1"),
      this.parse("Contact: \"Mr. Watson\" <sip:watson@worcester.bell-telephone.com>\r\n"
        + " ;q=0.7; expires=3600,\r\n"
        + " \"Mr. Watson\" <mailto:watson@bell-telephone.com> ;q=0.1\r\n"));
    assertEquals(new RawHeader(
      "Contact",
      "\"Mr. Watson\" <sip:watson@worcester.bell-telephone.com>\n"
        + " ;q=0.7; expires=3600,\n"
        + " \"Mr. Watson\" <mailto:watson@bell-telephone.com> ;q=0.1"),
      this.parse("Contact: \"Mr. Watson\" <sip:watson@worcester.bell-telephone.com>\n"
        + " ;q=0.7; expires=3600,\n"
        + " \"Mr. Watson\" <mailto:watson@bell-telephone.com> ;q=0.1\n"));
    assertEquals(new RawHeader("Date", "Sat, 13 Nov 2010 23:29:00 GMT"),
      this.parse("Date: Sat, 13 Nov 2010 23:29:00 GMT\n"));
    assertEquals(new RawHeader("From", "sip:+12125551212@server.phone2net.com;tag=887s"),
      this.parse("From: sip:+12125551212@server.phone2net.com;tag=887s\n"));
  }

  @Test
  public void testBad() {
    String test = "Accept-Encoding: gzip";
    assertEquals(null, this.parse(test, test.length()));
  }

}
