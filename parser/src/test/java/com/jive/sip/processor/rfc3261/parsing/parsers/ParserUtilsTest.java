package com.jive.sip.processor.rfc3261.parsing.parsers;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.parsers.core.ParserUtils;
import com.jive.sip.processor.rfc3261.RfcSipMessageManagerBuilder;
import com.jive.sip.processor.rfc3261.SipMessageManager;

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
    Assert.assertEquals(" ", this.parse(ParserUtils.LWS, " x", 1));
    Assert.assertEquals(" ", this.parse(ParserUtils.LWS, " "));
    Assert.assertEquals(" \n ", this.parse(ParserUtils.LWS, " \n "));
    Assert.assertEquals("\t", this.parse(ParserUtils.LWS, "\t"));
    Assert.assertEquals(" \r\n ", this.parse(ParserUtils.LWS, " \r\n "));
    Assert.assertEquals(" \n\t ", this.parse(ParserUtils.LWS, " \n\t "));
  }

  @Test
  public void parseString() {
    Assert.assertEquals("Hello, World!", this.parse(ParserUtils.str("Hello, World!"), "Hello, World!"));
    Assert.assertEquals(null, this.parse(ParserUtils.str("hello, World!"), "Hello, World!", 13));
    Assert.assertEquals("!", this.parse(ParserUtils.str("!"), "!"));
    Assert.assertEquals("", this.parse(ParserUtils.str(""), ""));
  }

  @Test
  public void parseUint() {
    final Parser<UnsignedInteger> parser = ParserUtils.uint(3, 5);
    Assert.assertEquals(UnsignedInteger.valueOf(234), this.parse(parser, "234"));
    Assert.assertEquals(UnsignedInteger.valueOf(2345), this.parse(parser, "2345"));
    Assert.assertEquals(UnsignedInteger.valueOf(23456), this.parse(parser, "23456"));
    Assert.assertEquals(UnsignedInteger.valueOf(23456), this.parse(parser, "234567", 1));
    Assert.assertEquals(null, this.parse(parser, "23", 2));
    Assert.assertEquals(null, this.parse(parser, "23s456", 6));
  }
}
