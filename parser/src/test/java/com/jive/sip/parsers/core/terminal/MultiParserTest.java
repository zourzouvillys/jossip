package com.jive.sip.parsers.core.terminal;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Range;

public class MultiParserTest {

  @Test
  public void test() {

    assertTrue(new MultiParser<String, Integer>(null, Range.atLeast(1)).canSupportMore(0));
    assertTrue(new MultiParser<String, Integer>(null, Range.atLeast(1)).canSupportMore(0));

    assertTrue(new MultiParser<String, Integer>(null, Range.singleton(1)).canSupportMore(0));
    assertFalse(new MultiParser<String, Integer>(null, Range.singleton(1)).canSupportMore(1));

    assertTrue(new MultiParser<String, Integer>(null, Range.atMost(3)).canSupportMore(0));
    assertFalse(new MultiParser<String, Integer>(null, Range.atMost(3)).canSupportMore(3));

    assertTrue(new MultiParser<String, Integer>(null, Range.closed(0, 3)).canSupportMore(0));
    assertFalse(new MultiParser<String, Integer>(null, Range.closed(0, 3)).canSupportMore(3));

  }

  @Test
  public void testSatisified() {

    assertFalse(new MultiParser<String, Integer>(null, Range.atLeast(1)).satifiedBy(0));
    assertTrue(new MultiParser<String, Integer>(null, Range.atLeast(1)).satifiedBy(1));

    assertFalse(new MultiParser<String, Integer>(null, Range.singleton(1)).satifiedBy(0));
    assertTrue(new MultiParser<String, Integer>(null, Range.singleton(1)).satifiedBy(1));

    assertTrue(new MultiParser<String, Integer>(null, Range.atMost(3)).satifiedBy(0));
    assertTrue(new MultiParser<String, Integer>(null, Range.atMost(3)).satifiedBy(3));
    assertFalse(new MultiParser<String, Integer>(null, Range.atMost(3)).satifiedBy(4));

    assertTrue(new MultiParser<String, Integer>(null, Range.closed(0, 3)).satifiedBy(0));
    assertFalse(new MultiParser<String, Integer>(null, Range.closed(0, 3)).satifiedBy(4));

  }

}
