/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.jive.sip.base.api.Token;
import com.jive.sip.message.api.headers.RetryAfter;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parameters.impl.TokenParameterDefinition;
import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.processor.rfc3261.parsing.SipMessageParseFailureException;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class RetryAfterParserTest extends BaseParserTest<RetryAfter> {

  public RetryAfterParserTest() {
    super(new RetryAfterParser());
  }

  @Test
  public void testRetryAfterHeader() throws SipMessageParseFailureException {
    RetryAfter header = this.parse("18000 (I'm (\\\"\\(\\\")in a meeting)  ;duration=3600");
    assertEquals("(I'm (\\\"\\(\\\")in a meeting)", header.getComment().get());
    assertEquals(Token.from("3600"), header.getParameter(new TokenParameterDefinition("duration")).get());
  }

  @Test
  public void testRetryAfterBadComment() throws SipMessageParseFailureException {
    assertEquals(new RetryAfter(83, null, DefaultParameters.from(Lists.<RawParameter>newArrayList())),
      this.parse("83 ((I'm (\\\"\\(\\\")in a meeting);duration=3600", 41));
  }

}
