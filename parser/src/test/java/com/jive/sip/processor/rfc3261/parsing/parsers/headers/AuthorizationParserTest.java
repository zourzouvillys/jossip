/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.jive.sip.auth.headers.Authorization;
import com.jive.sip.parameters.api.QuotedStringParameterValue;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.processor.rfc3261.parsing.SipMessageParseFailureException;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class AuthorizationParserTest extends BaseParserTest<Authorization> {

  public AuthorizationParserTest() {
    super(new AuthorizationParser());
  }

  /**
   * Test method for
   * {@link com.jive.sip.auth.headers.Authorization.AuthorizationHeader#AuthorizationHeader(java.lang.String, java.lang.String)}
   * .
   * 
   * @throws SipMessageParseFailureException
   */

  @Ignore
  @Test
  public void testAuthorizationHeader() throws SipMessageParseFailureException {

    final String test =
      "Digest username=\"Alice\", realm=\"atlanta.com\", "
        + "nonce=\"84a4cc6f3082121f32b42a2187831a9e\", response=\"7587245234b3434cc3412213e5f113a5432\"";

    assertEquals(new Authorization(
      "Digest",
      DefaultParameters.from(
        Lists.newArrayList(
          new RawParameter("username", new QuotedStringParameterValue("Alice")),
          new RawParameter("realm", new QuotedStringParameterValue("atlanta.com")),
          new RawParameter("nonce", new QuotedStringParameterValue("84a4cc6f3082121f32b42a2187831a9e")),
          new RawParameter("response", new QuotedStringParameterValue("7587245234b3434cc3412213e5f113a5432"))))),
      this.parse(test));
  }

}
