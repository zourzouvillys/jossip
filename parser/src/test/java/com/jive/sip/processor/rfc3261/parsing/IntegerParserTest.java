package com.jive.sip.processor.rfc3261.parsing;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.parsers.core.ParserUtils;

public class IntegerParserTest extends BaseParserTest<UnsignedInteger> {

  public IntegerParserTest() {
    super(ParserUtils.uint(1, 4));
  }

  @Test
  public void test() {
    Assert.assertEquals(UnsignedInteger.valueOf(10), this.parse("10"));
    Assert.assertEquals(UnsignedInteger.valueOf(0), this.parse("0"));
    Assert.assertEquals(UnsignedInteger.valueOf(1), this.parse("1"));
    Assert.assertEquals(UnsignedInteger.valueOf(1), this.parse("0001"));
    Assert.assertEquals(UnsignedInteger.valueOf(100), this.parse("100"));
    Assert.assertEquals(UnsignedInteger.valueOf(201), this.parse("201"));
  }

  @Test
  public void testParseOverLength() {
    Assert.assertEquals(UnsignedInteger.valueOf(0), this.parse("00001", 1));
  }

}
