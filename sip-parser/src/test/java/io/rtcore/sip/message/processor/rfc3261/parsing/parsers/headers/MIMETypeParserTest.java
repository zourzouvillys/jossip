/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.message.api.headers.MIMEType;
import io.rtcore.sip.message.parameters.impl.TokenParameterDefinition;
import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.processor.rfc3261.parsing.SipMessageParseFailureException;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.MIMETypeParser;

/**
 * 
 *
 */
public class MIMETypeParserTest extends BaseParserTest<MIMEType> {

  public MIMETypeParserTest() {
    super(new MIMETypeParser());
  }

  @Test
  public void testContentTypeHeaderConstructor() throws SipMessageParseFailureException {
    MIMEType header = this.parse("application/sdp");
    assertEquals("application", header.type());
    assertEquals("sdp", header.subType());

    // Better test
    header = this.parse("text/html; charset=ISO-8859-4; language-preference=\"fortran\"");
    assertEquals("text", header.type());
    assertEquals("html", header.subType());
    assertEquals(Token.from("ISO-8859-4"), header.getParameter(new TokenParameterDefinition("charset")).get());
    assertEquals(Token.from("fortran"), header.getParameter(new TokenParameterDefinition("language-preference")).get());
  }
}
