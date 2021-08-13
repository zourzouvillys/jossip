/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.message.api.headers.RetryAfter;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parameters.impl.TokenParameterDefinition;
import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.processor.rfc3261.parsing.SipMessageParseFailureException;

/**
 * 
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
