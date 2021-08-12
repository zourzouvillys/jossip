package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.message.api.headers.RValue;
import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.processor.rfc3261.parsing.SipMessageParseFailureException;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.RValueParser;

public class RValueParserTest extends BaseParserTest<RValue> {

  public RValueParserTest() {
    super(new RValueParser());
  }

  @Test
  public void testRetryAfterHeader() throws SipMessageParseFailureException {
    final RValue value = this.parse("moo.cows");
    assertEquals("moo", value.namespace());
    assertEquals("cows", value.priority());
  }

}
