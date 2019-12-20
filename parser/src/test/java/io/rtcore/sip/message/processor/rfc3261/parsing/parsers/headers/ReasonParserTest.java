package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.message.api.Reason;
import io.rtcore.sip.message.parameters.impl.TokenParameterDefinition;
import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.ReasonParser;

public class ReasonParserTest extends BaseParserTest<Reason> {

  public ReasonParserTest() {
    super(new ReasonParser());
  }

  @Test
  public void test() {
    final Reason reason = this.parse("Q.850;cause=12;text=\"Busy Everywhere\";xxx=yyy");
    assertEquals("Q.850", reason.protocol());
    assertEquals(12, (int) reason.cause().get());
    assertEquals("Busy Everywhere", reason.text().get());
    assertEquals(Token.from("yyy"), reason.getParameter(new TokenParameterDefinition("xxx")).get());
  }

}
