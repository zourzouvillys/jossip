/**
 *
 */
package com.jive.sip.message.api.header;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import com.jive.sip.base.api.Token;
import com.jive.sip.message.api.alertinfo.AlertInfoUriExtractor;
import com.jive.sip.message.api.alertinfo.HttpUriAlertInfo;
import com.jive.sip.message.api.headers.ParameterizedUri;
import com.jive.sip.parameters.impl.TokenParameterDefinition;
import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.processor.rfc3261.parsing.SipMessageParseFailureException;
import com.jive.sip.processor.rfc3261.parsing.parsers.headers.ParameterizedUriParser;
import com.jive.sip.processor.uri.RawUri;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */
public class ParameterizedUriParserTest extends BaseParserTest<ParameterizedUri> {
  public ParameterizedUriParserTest() {
    super(new ParameterizedUriParser());
  }

  @Test
  public void testCallInfo() throws SipMessageParseFailureException {
    final ParameterizedUri header = this.parse("<http://wwww.example.com/alice/photo.jpg> ;purpose  =  icon");
    assertEquals("http", header.getUri().getScheme());
    assertEquals("//wwww.example.com/alice/photo.jpg", ((RawUri) header.getUri()).getOpaque());
    assertTrue(header.getParameter(new TokenParameterDefinition("purpose")).isPresent());
    assertEquals(Token.from("icon"), header.getParameter(new TokenParameterDefinition("purpose")).get());
    assertEquals(new HttpUriAlertInfo("http://wwww.example.com/alice/photo.jpg"), header.getUri().apply(AlertInfoUriExtractor.getInstance()));
  }

  @Test
  public void testErrorInfo() throws SipMessageParseFailureException {
    final ParameterizedUri header = this.parse("<sip:not-in-service-recording@atlanta.com>");

    assertEquals("sip", header.getUri().getScheme());
    assertEquals("not-in-service-recording@atlanta.com", ((RawUri) header.getUri()).getOpaque());
  }
}
