package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import com.jive.sip.base.api.Token;
import com.jive.sip.message.api.Reason;
import com.jive.sip.parameters.impl.TokenParameterDefinition;
import com.jive.sip.parsers.core.BaseParserTest;

public class ReasonParserTest extends BaseParserTest<Reason> {

  public ReasonParserTest() {
    super(new ReasonParser());
  }

  @Test
  public void test() {
    final Reason reason = this.parse("Q.850;cause=12;text=\"Busy Everywhere\";xxx=yyy");
    assertEquals("Q.850", reason.getProtocol());
    assertEquals(12, (int) reason.getCause().get());
    assertEquals("Busy Everywhere", reason.getText().get());
    assertEquals(Token.from("yyy"), reason.getParameter(new TokenParameterDefinition("xxx")).get());
  }

}
