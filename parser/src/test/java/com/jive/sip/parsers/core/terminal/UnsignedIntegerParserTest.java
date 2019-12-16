package com.jive.sip.parsers.core.terminal;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.parsers.api.ParserInput;
import com.jive.sip.parsers.core.ByteParserInput;
import com.jive.sip.parsers.core.DefaultParserContext;

public class UnsignedIntegerParserTest {

  @Test
  public void test() {
    final ParserInput in = ByteParserInput.fromString("1");
    final ValueValidator<UnsignedInteger> validator = ValueValidator.expect(UnsignedInteger.valueOf(1));
    new UnsignedIntegerParser(1, 4).find(new DefaultParserContext(in), validator);
    Assert.assertEquals(UnsignedInteger.valueOf(1), validator.value());
  }

  @Test
  public void testTrailing() {
    final ParserInput in = ByteParserInput.fromString("1a");
    final ValueValidator<UnsignedInteger> validator = ValueValidator.expect(UnsignedInteger.valueOf(1));
    new UnsignedIntegerParser(1, 4).find(new DefaultParserContext(in), validator);
    Assert.assertEquals(UnsignedInteger.valueOf(1), validator.value());
    Assert.assertEquals(1, in.remaining());
  }

  @Test
  public void testLeading() {
    final ParserInput in = ByteParserInput.fromString("12229");
    final ValueValidator<UnsignedInteger> validator = ValueValidator.expect(UnsignedInteger.valueOf(1222));
    new UnsignedIntegerParser(1, 4).find(new DefaultParserContext(in), validator);
    Assert.assertEquals(1, in.remaining());
    Assert.assertEquals(UnsignedInteger.valueOf(1222), validator.value());
  }

}
