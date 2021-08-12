package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.uri;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.processor.uri.RawUri;
import io.rtcore.sip.message.uri.Uri;

public class UriParserTest extends BaseParserTest<Uri> {

  public UriParserTest() {
    super(UriParser.URI);
  }

  @Test
  public void test() {
    assertEquals(RawUri.of("unknown+scheme", "x"), this.parse("unknown+scheme:x"));
    assertEquals(RawUri.of("unknown+scheme", "//x"), this.parse("unknown+scheme://x"));
    assertEquals(RawUri.of("unknown+scheme", "/x?mo"), this.parse("unknown+scheme:/x?mo"));
    assertEquals(RawUri.of("tel", "+44"), this.parse("tel:+44"));
    assertEquals(RawUri.of("tel", "+44-1234-567890"), this.parse("tel:+44-1234-567890"));
    assertEquals(RawUri.of("sip", "theo"), this.parse("sip:theo"));
    assertEquals(RawUri.of("sip", "theo:bob@host"), this.parse("sip:theo:bob@host"));
    assertEquals(RawUri.of("sip", "theo:5060"), this.parse("sip:theo:5060"));
    assertEquals(RawUri.of("sip", "theo:bob@cows:5060"), this.parse("sip:theo:bob@cows:5060"));
    assertEquals(RawUri.of("sip", "theo;params=xx"), this.parse("sip:theo;params=xx"));
    assertEquals(RawUri.of("sip", "theo@jive.com?path"), this.parse("sip:theo@jive.com?path"));
  }

}
