/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jive.sip.base.api.RawHeader;
import com.jive.sip.base.api.RawMessage;
import com.jive.sip.parsers.core.BaseParserTest;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class DefaultRfcMessageParserTest extends BaseParserTest<RawMessage> {

  private final String test =
    "OPTIONS sip:carol@chicago.com SIP/2.0\r\n"
      + "Via: SIP/2.0/UDP pc33.atlanta.com;branch=z9hG4bKhjhs8ass877\r\n"
      + "Max-Forwards: 70\r\n"
      + "To: <sip:carol@chicago.com>\r\n"
      + "From: Alice <sip:alice@atlanta.com>;tag=1928301774\r\n"
      + "Call-ID: a84b4c76e66710\r\n"
      + "CSeq: 63104 OPTIONS\r\n"
      + "Contact: <sip:alice@pc33.atlanta.com>\r\n"
      + "Accept: application/sdp\r\n"
      + "Content-Length: 34\r\n"
      + "\r\n"
      + "This is the test body to a message";

  private final String lfTest =
    "OPTIONS sip:carol@chicago.com SIP/2.0\n"
      + "Via: SIP/2.0/UDP pc33.atlanta.com;branch=z9hG4bKhjhs8ass877\n"
      + "Max-Forwards: 70\n"
      + "To: <sip:carol@chicago.com>\n"
      + "From: Alice <sip:alice@atlanta.com>;tag=1928301774\n"
      + "Call-ID: a84b4c76e66710\n"
      + "CSeq: 63104 OPTIONS\n"
      + "Contact: <sip:alice@pc33.atlanta.com>\n"
      + "Accept: application/sdp\n"
      + "Content-Length: 34\n"
      + "\n"
      + "This is the test body to a message";

  private final RawMessage msg = RawMessage.create("OPTIONS sip:carol@chicago.com SIP/2.0");

  @BeforeEach
  public void setup() {
    this.msg.addHeader(new RawHeader("Via", "SIP/2.0/UDP pc33.atlanta.com;branch=z9hG4bKhjhs8ass877"));
    this.msg.addHeader(new RawHeader("Max-Forwards", "70"));
    this.msg.addHeader(new RawHeader("To", "<sip:carol@chicago.com>"));
    this.msg.addHeader(new RawHeader("From", "Alice <sip:alice@atlanta.com>;tag=1928301774"));
    this.msg.addHeader(new RawHeader("Call-ID", "a84b4c76e66710"));
    this.msg.addHeader(new RawHeader("CSeq", "63104 OPTIONS"));
    this.msg.addHeader(new RawHeader("Contact", "<sip:alice@pc33.atlanta.com>"));
    this.msg.addHeader(new RawHeader("Accept", "application/sdp"));
    this.msg.addHeader(new RawHeader("Content-Length", "34"));
    final String body = "This is the test body to a message";
    this.msg.setBody(body.getBytes());
  }

  /**
   * @param parser
   */
  public DefaultRfcMessageParserTest() {
    super(new DefaultRfcMessageParser());
  }

  /**
   * Test method for
   * {@link com.jive.sip.processor.rfc3261.parsing.DefaultRfcMessageParser#parse(org.jboss.netty.buffer.ChannelBuffer)}.
   */
  @Test
  public void testParseChannelBuffer() {
    assertEquals(this.msg, new DefaultRfcMessageParser().parse(ByteBuffer.wrap(this.test.getBytes())));
  }

  @Test
  public void testParseLfChannelBuffer() {
    assertEquals(this.msg, new DefaultRfcMessageParser().parse(ByteBuffer.wrap(this.lfTest.getBytes())));
  }

  /**
   * Test method for
   * {@link com.jive.sip.processor.rfc3261.parsing.DefaultRfcMessageParser#find(com.jive.sip.parsers.api.ParserContext, com.jive.sip.parsers.api.ValueListener)}
   * .
   */
  @Test
  public void testFind() {
    final RawMessage testMessage = this.parse(this.test);
    assertEquals(this.msg, testMessage);
  }

}
