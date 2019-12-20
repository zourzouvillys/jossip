/**
 *
 */
package io.rtcore.sip.message.processor.uri.parsers;

import static com.google.common.collect.Lists.newArrayList;
import static io.rtcore.sip.message.processor.uri.parsers.SipUriParser.HEADERS;
import static io.rtcore.sip.message.processor.uri.parsers.SipUriParser.USERINFO;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.parameters.api.FlagParameterValue;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.processor.uri.parsers.SipUriParser;
import io.rtcore.sip.message.uri.SipUri;
import io.rtcore.sip.message.uri.UserInfo;

/**
 * SIP-URI parsing tests.
 */

public class SipUriParserTest extends BaseParserTest<SipUri> {

  public SipUriParserTest() {
    super(SipUriParser.SIP);
  }

  @Test
  public void testUserinfo1() {

    assertEquals(
      UserInfo.of("+19073452063;phone-context=test"),
      this.parse(USERINFO, "+19073452063;phone-context=test@"));

  }

  @Test
  public void testUserinfo() {
    assertEquals(
      UserInfo.of("admin", "password"),
      this.parse(USERINFO, "admin:password@"));
  }

  @Test
  public void testHeaders() {
    assertEquals(
      newArrayList(new RawHeader("accept", "test"), new RawHeader("x-other", "")),
      this.parse(HEADERS, "?accept=test&x-other="));
  }

  @Test
  public void testLoweCaseEscapingHeaders() {
    this.parse("sip:1001@reg.jiveip.net?Replaces=bcf4c9c62c233272%3bto-tag%3d262F15AF-6A7C4D24%3bfrom-tag%3dd30e1a33e1");
  }

  @Test
  public void testSipParser() {

    assertEquals(
      new SipUri(SipUri.SIP, UserInfo.of("user"), HostAndPort.fromString("localhost")),
      this.parse("user@localhost"));

    assertEquals(
      new SipUri(SipUri.SIP, UserInfo.of("user", "password"), HostAndPort.fromString("localhost")),
      this.parse("user:password@localhost"));

    assertEquals(
      new SipUri(
        SipUri.SIP,
        UserInfo.of("user", "password"),
        HostAndPort.fromString("localhost"),
        DefaultParameters
          .from(
            newArrayList(
              new RawParameter("param1", new TokenParameterValue("value1")),
              new RawParameter("param2", new FlagParameterValue()))),
        newArrayList(new RawHeader("accept", "test"), new RawHeader("x-other", ""))),

      this.parse("user:password@localhost;param1=value1;param2?accept=test&x-other="));

  }

}
