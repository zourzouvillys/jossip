package com.jive.sip.message.api.header;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.jive.sip.base.api.Token;
import com.jive.sip.message.api.NameAddr;
import com.jive.sip.parameters.api.FlagParameterValue;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.TokenParameterValue;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.processor.rfc3261.parsing.parsers.headers.NameAddrParser;
import com.jive.sip.processor.uri.RawUri;
import com.jive.sip.processor.uri.SipUriExtractor;
import com.jive.sip.processor.uri.TelUriExtractor;

public class NameAddrParserTest extends BaseParserTest<NameAddr> {
  List<RawParameter> params;
  List<RawParameter> simpleParams =
    Lists.newArrayList(new RawParameter(
      Token.from("tag"),
      new TokenParameterValue(
        "xxxx")));

  public NameAddrParserTest() {
    super(new NameAddrParser());
  }

  @BeforeEach
  public void setup() {
    this.params =
      Lists.newArrayList(new RawParameter(Token.from("a"), new TokenParameterValue("1")),
        new RawParameter(
          Token.from("b"),
          new FlagParameterValue()),
        new RawParameter(Token.from("c"), new TokenParameterValue("c")));
  }

  @Test
  public void testLowerCase() {
    this.parse("<sip:1002@reg.jiveip.net;user=phone?Replaces=bcf4c9c62c233272%3bto-tag%3d262F15AF-6A7C4D24%3bfrom-tag%3dd30e1a33e1>");
  }

  @Test
  public void testQuotedDisplayName() {
    final NameAddr name = this.parse("\"Alice\" <sip:alice@domain> ;a=1 ; b;c = c");
    assertTrue(name.getName().isPresent());
    assertEquals("Alice", name.getName().get());
    assertEquals(new RawUri("sip", "alice@domain"), name.getAddress());
    assertEquals(DefaultParameters.from(this.params), name.getParameters().get());
  }

  @Test
  public void testDisplayNameAndAsinineSpacing() {
    final NameAddr name = this.parse("Alice <sip:alice@domain> ;a=1 ; b;c = c");
    assertEquals("Alice", name.getName().get());
    assertEquals(new RawUri("sip", "alice@domain"), name.getAddress());
    assertEquals(DefaultParameters.from(this.params), name.getParameters().get());
  }

  @Test
  public void testNoDisplayNameWithAsinineSpacing() {
    final NameAddr name = this.parse("<sip:alice@domain> ;a=1 ; b;c = c");
    assertFalse(name.getName().isPresent());
    assertEquals(new RawUri("sip", "alice@domain"), name.getAddress());
    assertEquals(DefaultParameters.from(this.params), name.getParameters().get());
  }

  @Test
  public void testUriNoBrackets() {
    final NameAddr name = this.parse("sip:alice@domain");
    assertFalse(name.getName().isPresent());
    assertFalse(name.getParameters().isPresent());
    assertEquals(new RawUri("sip", "alice@domain"), name.getAddress());
  }

  @Test
  public void testSimpleDisplayName() {
    final NameAddr name = this.parse("Anonymous <sip:c8oqz84zk7z@privacy.org>;tag=hyh8");
    assertTrue(name.getName().isPresent());
    assertEquals("Anonymous", name.getName().get());
    assertEquals(new RawUri("sip", "c8oqz84zk7z@privacy.org"), name.getAddress());
    assertEquals(DefaultParameters.from(Lists.newArrayList(new RawParameter("tag", new TokenParameterValue("hyh8")))),
      name.getParameters().get());
  }

  @Test
  public void testDisplayNamesWithMultipleParts() {
    final NameAddr name = this.parse("Theo Zourzouvillys <sip:theo@jive.com>;tag=xxxx");
    assertTrue(name.getName().isPresent());
    assertEquals("Theo Zourzouvillys", name.getName().get());
    assertEquals(new RawUri("sip", "theo@jive.com"), name.getAddress());
    assertEquals(DefaultParameters.from(this.simpleParams), name.getParameters().get());
  }

  @Test
  public void testDisplayNamesWithBrokenBits() {
    final NameAddr name = this.parse("Theo (Zourzouvillys) <sip:theo@jive.com>;tag=xxxx");
    assertTrue(name.getName().isPresent());
    assertEquals("Theo (Zourzouvillys)", name.getName().get());
    assertEquals(new RawUri("sip", "theo@jive.com"), name.getAddress());
    assertEquals(DefaultParameters.from(this.simpleParams), name.getParameters().get());
  }

  @Test
  public void testNoDiplayNameWithSpecialUri() {
    final NameAddr name = this.parse("<sip:theo@jive.com?hello>;tag=xxxx");
    assertFalse(name.getName().isPresent());
    assertEquals(new RawUri("sip", "theo@jive.com?hello"), name.getAddress());
    assertEquals(DefaultParameters.from(this.simpleParams), name.getParameters().get());
  }

  @Test
  public void testUriParameters() {
    final NameAddr name = this.parse("Theo <sip:theo@jive.com;lr>;tag=xxxx");
    assertTrue(name.getName().isPresent());
    assertEquals(new RawUri("sip", "theo@jive.com;lr"), name.getAddress());
    assertEquals(DefaultParameters.from(this.simpleParams), name.getParameters().get());
    assertEquals(DefaultParameters.from(Lists.newArrayList(new RawParameter("lr"))),
      name.getAddress().apply(SipUriExtractor.getInstance()).getParameters().get());
  }

  @Test
  public void testNoDisplayNameNoBracketsSpecialUri() {
    this.parse("sip:theo@jive.com?hello", 6);
  }

  @Test
  public void testNoBracketsAndParameters() {
    final NameAddr name = this.parse("sip:theo@jive.com;tag=xxxx");
    assertFalse(name.getName().isPresent());
    assertEquals(new RawUri("sip", "theo@jive.com"), name.getAddress());
    assertEquals(DefaultParameters.from(this.simpleParams), name.getParameters().get());
  }

  @Test
  public void testDisplayNameNoBrackets() {
    this.parse("Theo sip:theo@jive.com;tag=xxxx", 31);
  }

  @Test
  public void testReservedCharsInQuotedName() {
    final NameAddr name = this.parse("\"Theo??\" <sip:theo@jive.com>");
    assertTrue(name.getName().isPresent());
    assertEquals("Theo??", name.getName().get());
    assertEquals(new RawUri("sip", "theo@jive.com"), name.getAddress());
  }

  @Test
  public void testTelUri() {
    final NameAddr name = this.parse("\"Theo\" <tel:+18009453669>;Phone-Context=pbx");
    assertTrue(name.getName().isPresent());
    assertEquals(name.getName().get(), "Theo");
    assertEquals(new RawUri("tel", "+18009453669"), name.getAddress());
    assertEquals(
      DefaultParameters.from(Lists.newArrayList(new RawParameter("Phone-Context", new TokenParameterValue("pbx")))),
      name.getParameters().get());
  }

  @Test
  public void testStaticParsing() {
    final NameAddr value = NameAddrParser.parse("\"Theo\" <tel:+18009453669>;Phone-Context=pbx");
    assertTrue(value.getName().isPresent());
    assertEquals("Theo", value.getName().orElse(null));
    assertEquals("tel", value.getAddress().getScheme());
    assertEquals("+18009453669",
      value.getAddress()
        .apply(TelUriExtractor.getInstance())
        .getNumber());
    assertTrue(value.getParameters().isPresent());
    Parameters parameters = value.getParameters().get();
    assertTrue(parameters.contains("Phone-Context"));
    assertEquals("pbx", parameters.getParameter("Phone-Context").get());
  }

}
