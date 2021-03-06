package io.rtcore.sip.message.message.api.header;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.parameters.api.FlagParameterValue;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.NameAddrParser;
import io.rtcore.sip.message.processor.uri.RawUri;
import io.rtcore.sip.message.processor.uri.SipUriExtractor;
import io.rtcore.sip.message.processor.uri.TelUriExtractor;

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
    assertEquals(RawUri.of("sip", "alice@domain"), name.address());
    assertEquals(DefaultParameters.from(this.params), name.getParameters().get());
  }

  @Test
  public void testDisplayNameAndAsinineSpacing() {
    final NameAddr name = this.parse("Alice <sip:alice@domain> ;a=1 ; b;c = c");
    assertEquals("Alice", name.getName().get());
    assertEquals(RawUri.of("sip", "alice@domain"), name.address());
    assertEquals(DefaultParameters.from(this.params), name.getParameters().get());
  }

  @Test
  public void testNoDisplayNameWithAsinineSpacing() {
    final NameAddr name = this.parse("<sip:alice@domain> ;a=1 ; b;c = c");
    assertFalse(name.getName().isPresent());
    assertEquals(RawUri.of("sip", "alice@domain"), name.address());
    assertEquals(DefaultParameters.from(this.params), name.getParameters().get());
  }

  @Test
  public void testUriNoBrackets() {
    final NameAddr name = this.parse("sip:alice@domain");
    assertFalse(name.getName().isPresent());
    assertFalse(name.getParameters().isPresent());
    assertEquals(RawUri.of("sip", "alice@domain"), name.address());
  }

  @Test
  public void testSimpleDisplayName() {
    final NameAddr name = this.parse("Anonymous <sip:c8oqz84zk7z@privacy.org>;tag=hyh8");
    assertTrue(name.getName().isPresent());
    assertEquals("Anonymous", name.getName().get());
    assertEquals(RawUri.of("sip", "c8oqz84zk7z@privacy.org"), name.address());
    assertEquals(DefaultParameters.from(Lists.newArrayList(new RawParameter("tag", new TokenParameterValue("hyh8")))),
      name.getParameters().get());
  }

  @Test
  public void testDisplayNamesWithMultipleParts() {
    final NameAddr name = this.parse("Theo Zourzouvillys <sip:theo@jive.com>;tag=xxxx");
    assertTrue(name.getName().isPresent());
    assertEquals("Theo Zourzouvillys", name.getName().get());
    assertEquals(RawUri.of("sip", "theo@jive.com"), name.address());
    assertEquals(DefaultParameters.from(this.simpleParams), name.getParameters().get());
  }

  @Test
  public void testDisplayNamesWithBrokenBits() {
    final NameAddr name = this.parse("Theo (Zourzouvillys) <sip:theo@jive.com>;tag=xxxx");
    assertTrue(name.getName().isPresent());
    assertEquals("Theo (Zourzouvillys)", name.getName().get());
    assertEquals(RawUri.of("sip", "theo@jive.com"), name.address());
    assertEquals(DefaultParameters.from(this.simpleParams), name.getParameters().get());
  }

  @Test
  public void testNoDiplayNameWithSpecialUri() {
    final NameAddr name = this.parse("<sip:theo@jive.com?hello>;tag=xxxx");
    assertFalse(name.getName().isPresent());
    assertEquals(RawUri.of("sip", "theo@jive.com?hello"), name.address());
    assertEquals(DefaultParameters.from(this.simpleParams), name.getParameters().get());
  }

  @Test
  public void testUriParameters() {
    final NameAddr name = this.parse("Theo <sip:theo@jive.com;lr>;tag=xxxx");
    assertTrue(name.getName().isPresent());
    assertEquals(RawUri.of("sip", "theo@jive.com;lr"), name.address());
    assertEquals(DefaultParameters.from(this.simpleParams), name.getParameters().get());
    assertEquals(DefaultParameters.from(Lists.newArrayList(new RawParameter("lr"))),
      name.address().apply(SipUriExtractor.getInstance()).getParameters().get());
  }

  @Test
  public void testNoDisplayNameNoBracketsSpecialUri() {
    this.parse("sip:theo@jive.com?hello", 6);
  }

  @Test
  public void testNoBracketsAndParameters() {
    final NameAddr name = this.parse("sip:theo@jive.com;tag=xxxx");
    assertFalse(name.getName().isPresent());
    assertEquals(RawUri.of("sip", "theo@jive.com"), name.address());
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
    assertEquals(RawUri.of("sip", "theo@jive.com"), name.address());
  }

  @Test
  public void testTelUri() {
    final NameAddr name = this.parse("\"Theo\" <tel:+18009453669>;Phone-Context=pbx");
    assertTrue(name.getName().isPresent());
    assertEquals(name.getName().get(), "Theo");
    assertEquals(RawUri.of("tel", "+18009453669"), name.address());
    assertEquals(
      DefaultParameters.from(Lists.newArrayList(new RawParameter("Phone-Context", new TokenParameterValue("pbx")))),
      name.getParameters().get());
  }

  @Test
  public void testStaticParsing() {
    final NameAddr value = NameAddrParser.parse("\"Theo\" <tel:+18009453669>;Phone-Context=pbx");
    assertTrue(value.getName().isPresent());
    assertEquals("Theo", value.getName().orElse(null));
    assertEquals("tel", value.address().getScheme());
    assertEquals("+18009453669",
      value.address()
        .apply(TelUriExtractor.getInstance())
        .number());
    assertTrue(value.getParameters().isPresent());
    Parameters parameters = value.getParameters().get();
    assertTrue(parameters.contains("Phone-Context"));
    assertEquals("pbx", parameters.getParameter("Phone-Context").get());
  }

}
