package io.rtcore.sip.message.processor.rfc3261.parsing.parsers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.parsers.core.ParserUtils;
import io.rtcore.sip.message.processor.rfc3261.RfcSipMessageManagerBuilder;
import io.rtcore.sip.message.processor.rfc3261.SipMessageManager;

public class ParserUtilsTest extends BaseParserTest<CharSequence> {

  /**
   * @param parser
   */
  public ParserUtilsTest() {
    super(null);
  }

  SipMessageManager manager = new RfcSipMessageManagerBuilder().build();

  @Test
  public void test() {
    assertEquals(" ", this.parse(ParserUtils.LWS, " x", 1));
    assertEquals(" ", this.parse(ParserUtils.LWS, " "));
    assertEquals(" \n ", this.parse(ParserUtils.LWS, " \n "));
    assertEquals("\t", this.parse(ParserUtils.LWS, "\t"));
    assertEquals(" \r\n ", this.parse(ParserUtils.LWS, " \r\n "));
    assertEquals(" \n\t ", this.parse(ParserUtils.LWS, " \n\t "));
  }

  @Test
  public void parseString() {
    assertEquals("Hello, World!", this.parse(ParserUtils.str("Hello, World!"), "Hello, World!"));
    assertEquals(null, this.parse(ParserUtils.str("hello, World!"), "Hello, World!", 13));
    assertEquals("!", this.parse(ParserUtils.str("!"), "!"));
    assertEquals("", this.parse(ParserUtils.str(""), ""));
  }

  @Test
  public void parseUint() {
    final Parser<UnsignedInteger> parser = ParserUtils.uint(3, 5);
    assertEquals(UnsignedInteger.valueOf(234), this.parse(parser, "234"));
    assertEquals(UnsignedInteger.valueOf(2345), this.parse(parser, "2345"));
    assertEquals(UnsignedInteger.valueOf(23456), this.parse(parser, "23456"));
    assertEquals(UnsignedInteger.valueOf(23456), this.parse(parser, "234567", 1));
    assertEquals(null, this.parse(parser, "23", 2));
    assertEquals(null, this.parse(parser, "23s456", 6));
  }
}
