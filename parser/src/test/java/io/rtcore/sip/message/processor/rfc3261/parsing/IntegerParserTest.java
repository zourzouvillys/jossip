package io.rtcore.sip.message.processor.rfc3261.parsing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.parsers.core.ParserUtils;

public class IntegerParserTest extends BaseParserTest<UnsignedInteger> {

  public IntegerParserTest() {
    super(ParserUtils.uint(1, 4));
  }

  @Test
  public void test() {
    assertEquals(UnsignedInteger.valueOf(10), this.parse("10"));
    assertEquals(UnsignedInteger.valueOf(0), this.parse("0"));
    assertEquals(UnsignedInteger.valueOf(1), this.parse("1"));
    assertEquals(UnsignedInteger.valueOf(1), this.parse("0001"));
    assertEquals(UnsignedInteger.valueOf(100), this.parse("100"));
    assertEquals(UnsignedInteger.valueOf(201), this.parse("201"));
  }

  @Test
  public void testParseOverLength() {
    assertEquals(UnsignedInteger.valueOf(0), this.parse("00001", 1));
  }

}
