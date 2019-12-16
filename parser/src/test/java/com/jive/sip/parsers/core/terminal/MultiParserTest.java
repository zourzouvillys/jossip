package com.jive.sip.parsers.core.terminal;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Range;

public class MultiParserTest {

  @Test
  public void test() {

    Assert.assertTrue(new MultiParser<String, Integer>(null, Range.atLeast(1)).canSupportMore(0));
    Assert.assertTrue(new MultiParser<String, Integer>(null, Range.atLeast(1)).canSupportMore(0));

    Assert.assertTrue(new MultiParser<String, Integer>(null, Range.singleton(1)).canSupportMore(0));
    Assert.assertFalse(new MultiParser<String, Integer>(null, Range.singleton(1)).canSupportMore(1));

    Assert.assertTrue(new MultiParser<String, Integer>(null, Range.atMost(3)).canSupportMore(0));
    Assert.assertFalse(new MultiParser<String, Integer>(null, Range.atMost(3)).canSupportMore(3));

    Assert.assertTrue(new MultiParser<String, Integer>(null, Range.closed(0, 3)).canSupportMore(0));
    Assert.assertFalse(new MultiParser<String, Integer>(null, Range.closed(0, 3)).canSupportMore(3));

  }

  @Test
  public void testSatisified() {

    Assert.assertFalse(new MultiParser<String, Integer>(null, Range.atLeast(1)).satifiedBy(0));
    Assert.assertTrue(new MultiParser<String, Integer>(null, Range.atLeast(1)).satifiedBy(1));

    Assert.assertFalse(new MultiParser<String, Integer>(null, Range.singleton(1)).satifiedBy(0));
    Assert.assertTrue(new MultiParser<String, Integer>(null, Range.singleton(1)).satifiedBy(1));

    Assert.assertTrue(new MultiParser<String, Integer>(null, Range.atMost(3)).satifiedBy(0));
    Assert.assertTrue(new MultiParser<String, Integer>(null, Range.atMost(3)).satifiedBy(3));
    Assert.assertFalse(new MultiParser<String, Integer>(null, Range.atMost(3)).satifiedBy(4));

    Assert.assertTrue(new MultiParser<String, Integer>(null, Range.closed(0, 3)).satifiedBy(0));
    Assert.assertFalse(new MultiParser<String, Integer>(null, Range.closed(0, 3)).satifiedBy(4));

  }

}
