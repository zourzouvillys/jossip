package com.jive.sip.processor.rfc3261.parsing.parsers.uri;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.processor.uri.RawUri;
import com.jive.sip.uri.api.Uri;

public class UriParserTest extends BaseParserTest<Uri> {

  public UriParserTest() {
    super(UriParser.URI);
  }

  @Test
  public void test() {
    assertEquals(new RawUri("unknown+scheme", "x"), this.parse("unknown+scheme:x"));
    assertEquals(new RawUri("unknown+scheme", "//x"), this.parse("unknown+scheme://x"));
    assertEquals(new RawUri("unknown+scheme", "/x?mo"), this.parse("unknown+scheme:/x?mo"));
    assertEquals(new RawUri("tel", "+44"), this.parse("tel:+44"));
    assertEquals(new RawUri("tel", "+44-1234-567890"), this.parse("tel:+44-1234-567890"));
    assertEquals(new RawUri("sip", "theo"), this.parse("sip:theo"));
    assertEquals(new RawUri("sip", "theo:bob@host"), this.parse("sip:theo:bob@host"));
    assertEquals(new RawUri("sip", "theo:5060"), this.parse("sip:theo:5060"));
    assertEquals(new RawUri("sip", "theo:bob@cows:5060"), this.parse("sip:theo:bob@cows:5060"));
    assertEquals(new RawUri("sip", "theo;params=xx"), this.parse("sip:theo;params=xx"));
    assertEquals(new RawUri("sip", "theo@jive.com?path"), this.parse("sip:theo@jive.com?path"));
  }

}
