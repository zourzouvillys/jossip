/**
 * 
 */
package com.jive.sip.message.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.jive.sip.base.api.RawHeader;
import com.jive.sip.base.api.RawMessage;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */
public class RawMessageTest {

  @Test
  public void test() {
    RawMessage msg1 = RawMessage.create("OPTIONS sip:carol@chicago.com SIP/2.0");
    msg1.addHeader(new RawHeader("Via", "SIP/2.0/UDP pc33.atlanta.com;branch=z9hG4bKhjhs8ass877"));
    msg1.addHeader(new RawHeader("Max-Forwards", "70"));
    msg1.addHeader(new RawHeader("To", "<sip:carol@chicago.com>"));
    msg1.addHeader(new RawHeader("From", "Alice <sip:alice@atlanta.com>;tag=1928301774"));
    msg1.addHeader(new RawHeader("Call-ID", "a84b4c76e66710"));
    msg1.addHeader(new RawHeader("CSeq", "63104 OPTIONS"));
    msg1.addHeader(new RawHeader("Contact", "<sip:alice@pc33.atlanta.com>"));
    msg1.addHeader(new RawHeader("Accept", "application/sdp"));
    msg1.addHeader(new RawHeader("Content-Length", "0"));

    RawMessage msg2 = RawMessage.create("OPTIONS sip:carol@chicago.com SIP/2.0");
    msg2.addHeader(new RawHeader("Via", "SIP/2.0/UDP pc33.atlanta.com;branch=z9hG4bKhjhs8ass877"));
    msg2.addHeader(new RawHeader("Max-Forwards", "70"));
    msg2.addHeader(new RawHeader("To", "<sip:carol@chicago.com>"));
    msg2.addHeader(new RawHeader("From", "Alice <sip:alice@atlanta.com>;tag=1928301774"));
    msg2.addHeader(new RawHeader("Call-ID", "a84b4c76e66710"));
    msg2.addHeader(new RawHeader("CSeq", "63104 OPTIONS"));
    msg2.addHeader(new RawHeader("Contact", "<sip:alice@pc33.atlanta.com>"));
    msg2.addHeader(new RawHeader("Accept", "application/sdp"));
    msg2.addHeader(new RawHeader("Content-Length", "0"));

    assertEquals(msg1, msg2);
  }

}
