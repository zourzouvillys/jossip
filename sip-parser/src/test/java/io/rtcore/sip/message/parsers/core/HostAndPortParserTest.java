/**
 * 
 */
package io.rtcore.sip.message.parsers.core;

import static io.rtcore.sip.message.parsers.core.HostAndPortParser.DOMAIN_LABEL;
import static io.rtcore.sip.message.parsers.core.HostAndPortParser.HEXPART;
import static io.rtcore.sip.message.parsers.core.HostAndPortParser.HOSTNAME;
import static io.rtcore.sip.message.parsers.core.HostAndPortParser.IPV4_ADDRESS;
import static io.rtcore.sip.message.parsers.core.HostAndPortParser.IPV6_REFFERENCE;
import static io.rtcore.sip.message.parsers.core.HostAndPortParser.TOP_LABEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.google.common.net.HostAndPort;

/**
 * 
 * 
 */
public class HostAndPortParserTest extends BaseParserTest<HostAndPort> {

  public HostAndPortParserTest() {
    super(HostAndPortParser.INSTANCE);
  }

  @Test
  public void testIPV6() {
    assertEquals("[FD00::1337:C96:DB:3:6C91]",
      this.parse(IPV6_REFFERENCE, "[FD00::1337:C96:DB:3:6C91]"));
  }

  @Test
  public void testDomainLabel() {
    assertEquals(null, this.parse(DOMAIN_LABEL, "-", 1));
    assertEquals("a", this.parse(DOMAIN_LABEL, "a"));
    assertEquals("1", this.parse(DOMAIN_LABEL, "1"));
    assertEquals("1", this.parse(DOMAIN_LABEL, "1-", 1));
    assertEquals("1-a", this.parse(DOMAIN_LABEL, "1-a"));
    assertEquals("b--3-1a", this.parse(DOMAIN_LABEL, "b--3-1a"));
    assertEquals("1--3-1a", this.parse(DOMAIN_LABEL, "1--3-1a"));
  }

  @Test
  public void testTopLabel() {
    assertEquals(null, this.parse(TOP_LABEL, "-", 1));
    assertEquals("a", this.parse(TOP_LABEL, "a"));
    assertEquals(null, this.parse(TOP_LABEL, "1", 1));
    assertEquals("f", this.parse(TOP_LABEL, "f-", 1));
    assertEquals("r-a", this.parse(TOP_LABEL, "r-a"));
    assertEquals("b-1", this.parse(TOP_LABEL, "b-1"));
    assertEquals(null, this.parse(TOP_LABEL, "1-b", 3));
  }

  @Test
  public void testHostname() {
    assertEquals("this.is.a.test", this.parse(HOSTNAME, "this.is.a.test"));
    assertEquals(null, this.parse(HOSTNAME, "0000", 4));
    assertEquals(null, this.parse(HOSTNAME, "-moo", 4));
    assertEquals("moo", this.parse(HOSTNAME, "moo"));
    assertEquals("moo.", this.parse(HOSTNAME, "moo."));
    assertEquals("another.test.", this.parse(HOSTNAME, "another.test.1", 1));

  }

  @Test
  public void testHostnameParserDoesntParseIpAddress() {
    assertEquals(null, this.parse(HOSTNAME, "127.0.0.1", 9));
  }

  @Test
  public void testIPv4Address() {
    assertEquals("127.0.0.1", this.parse(IPV4_ADDRESS, "127.0.0.1"));
    assertEquals("127.0.23.1", this.parse(IPV4_ADDRESS, "127.0.23.1:2000", 5));
    assertEquals(null, this.parse(IPV4_ADDRESS, "127.0.23.", 9));
    assertEquals(null, this.parse(IPV4_ADDRESS, "127.0a.23.1", 11));
    assertEquals(null, this.parse(IPV4_ADDRESS, "127.0000.23.1", 13));
    assertEquals(null, this.parse(IPV4_ADDRESS, "127..23.1", 9));
    assertEquals(null, this.parse(IPV4_ADDRESS, "265.1.1.1", 9));
    assertEquals("0.0.0.0", this.parse(IPV4_ADDRESS, "0.0.0.0"));
  }

  @Test
  public void testHexpart() {
    assertEquals("03AD", this.parse(HEXPART, "03AD"));
    assertEquals("::03AD", this.parse(HEXPART, "::03AD"));
    assertEquals("D34B::03AD", this.parse(HEXPART, "D34B::03AD"));
    assertEquals("::D34B", this.parse(HEXPART, "::D34B::03AD", 6));
    assertEquals("::D34B:03AD", this.parse(HEXPART, "::D34B:03AD"));
    assertEquals("::D34B:03AD", this.parse(HEXPART, "::D34B:03AD:", 1));
    assertEquals("::", this.parse(HEXPART, "::"));
  }

  @Test
  public void testHostAndPortParser() {
    assertEquals(HostAndPort.fromParts("this.is.a.test", 2000), this.parse("this.is.a.test:2000"));
    assertEquals(HostAndPort.fromString("this.is.a.test"), this.parse("this.is.a.test"));
    assertEquals(HostAndPort.fromParts("this.is.a.test", 2000), this.parse("this.is.a.test:2000"));
    assertEquals(HostAndPort.fromParts("127.0.0.1", 2000), this.parse("127.0.0.1:2000"));
    assertEquals(HostAndPort.fromString("127.0.0.1"), this.parse("127.0.0.1"));
    assertNull(this.parse("this.is.a.test:65536", 20));
    assertEquals(HostAndPort.fromString("[::12BD:127.0.0.1]"), this.parse("[::12BD:127.0.0.1]"));
  }

}
