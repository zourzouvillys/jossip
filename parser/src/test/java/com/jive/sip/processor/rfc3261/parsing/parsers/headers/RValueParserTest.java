package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jive.sip.message.api.headers.RValue;
import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.processor.rfc3261.parsing.SipMessageParseFailureException;

public class RValueParserTest extends BaseParserTest<RValue>
{

  public RValueParserTest()
  {
    super(new RValueParser());
  }

  @Test
  public void testRetryAfterHeader() throws SipMessageParseFailureException
  {
    final RValue value = this.parse("moo.cows");
    assertEquals("moo", value.getNamespace());
    assertEquals("cows", value.getPriority());
  }



}
