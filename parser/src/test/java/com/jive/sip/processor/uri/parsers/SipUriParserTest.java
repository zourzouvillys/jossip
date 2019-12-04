/**
 *
 */
package com.jive.sip.processor.uri.parsers;

import static com.jive.sip.processor.uri.parsers.SipUriParser.HEADERS;
import static com.jive.sip.processor.uri.parsers.SipUriParser.USERINFO;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.parameters.api.FlagParameterValue;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.TokenParameterValue;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.uri.api.SipUri;
import com.jive.sip.uri.api.UserInfo;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */
public class SipUriParserTest extends BaseParserTest<SipUri>
{

  public SipUriParserTest()
  {
    super(SipUriParser.SIP);
  }

  @Test
  public void testUserinfo()
  {
    assertEquals(new UserInfo("+19073452063;phone-context=test"), this.parse(
        USERINFO, "+19073452063;phone-context=test@"));
    assertEquals(new UserInfo("admin", "password"), this.parse(USERINFO, "admin:password@"));
  }

  @Test
  public void testHeaders()
  {
    assertEquals(Lists.newArrayList(new RawHeader("accept", "test"), new RawHeader("x-other", "")),
        this.parse(HEADERS, "?accept=test&x-other="));
  }

  @Test
  public void testLoweCaseEscapingHeaders()
  {
    this.parse("sip:1001@reg.jiveip.net?Replaces=bcf4c9c62c233272%3bto-tag%3d262F15AF-6A7C4D24%3bfrom-tag%3dd30e1a33e1");
  }

  @Test
  public void testSipParser()
  {
    assertEquals(new SipUri(SipUri.SIP, new UserInfo("user"), HostAndPort.fromString("localhost")),
        this.parse("user@localhost"));
    assertEquals(new SipUri(SipUri.SIP, new UserInfo("user", "password"), HostAndPort.fromString("localhost")),
        this.parse("user:password@localhost"));
    assertEquals(
        new SipUri(SipUri.SIP,
            new UserInfo("user", "password"),
            HostAndPort.fromString("localhost"),
            DefaultParameters.from(Lists.newArrayList(new RawParameter("param1", new TokenParameterValue("value1")),
                new RawParameter("param2", new FlagParameterValue()))),
                Lists.newArrayList(new RawHeader("accept", "test"), new RawHeader("x-other", ""))),
                this.parse("user:password@localhost;param1=value1;param2?accept=test&x-other="));
  }

}
