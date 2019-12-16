/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.jive.sip.base.api.Token;
import com.jive.sip.message.api.headers.MIMEType;
import com.jive.sip.parameters.impl.TokenParameterDefinition;
import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.processor.rfc3261.parsing.SipMessageParseFailureException;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
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
