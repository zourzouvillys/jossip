/**
 *
 */
package io.rtcore.sip.message.message.api.header;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.message.api.alertinfo.AlertInfoUriExtractor;
import io.rtcore.sip.message.message.api.alertinfo.HttpUriAlertInfo;
import io.rtcore.sip.message.message.api.headers.ParameterizedUri;
import io.rtcore.sip.message.parameters.impl.TokenParameterDefinition;
import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.processor.rfc3261.parsing.SipMessageParseFailureException;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.ParameterizedUriParser;
import io.rtcore.sip.message.processor.uri.RawUri;

/**
 * 
 *
 */
public class ParameterizedUriParserTest extends BaseParserTest<ParameterizedUri> {
  public ParameterizedUriParserTest() {
    super(new ParameterizedUriParser());
  }

  @Test
  public void testCallInfo() throws SipMessageParseFailureException {
    final ParameterizedUri header = this.parse("<http://wwww.example.com/alice/photo.jpg> ;purpose  =  icon");
    assertEquals("http", header.uri().getScheme());
    assertEquals("//wwww.example.com/alice/photo.jpg", ((RawUri) header.uri()).getOpaque());
    assertTrue(header.getParameter(new TokenParameterDefinition("purpose")).isPresent());
    assertEquals(Token.from("icon"), header.getParameter(new TokenParameterDefinition("purpose")).get());
    assertEquals(new HttpUriAlertInfo("http://wwww.example.com/alice/photo.jpg"), header.uri().apply(AlertInfoUriExtractor.getInstance()));
  }

  @Test
  public void testErrorInfo() throws SipMessageParseFailureException {
    final ParameterizedUri header = this.parse("<sip:not-in-service-recording@atlanta.com>");

    assertEquals("sip", header.uri().getScheme());
    assertEquals("not-in-service-recording@atlanta.com", ((RawUri) header.uri()).getOpaque());
  }
}
