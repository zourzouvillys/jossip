/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jive.sip.base.api.RawHeader;
import com.jive.sip.base.api.RawMessage;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.processor.rfc3261.RfcSipMessageManager;
import com.jive.sip.processor.rfc3261.SipMessageManager;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManagerBuilder;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */
public class SipRequestSerializerTest {
  private String result =
    "OPTIONS sip:carol@chicago.com SIP/2.0\r\n"
      + "Via: SIP/2.0/UDP pc33.atlanta.com;branch=z9hG4bKhjhs8ass877\r\n"
      + "Max-Forwards: 70\r\n"
      + "To: <sip:carol@chicago.com>\r\n"
      + "From: Alice <sip:alice@atlanta.com>;tag=1928301774\r\n"
      + "Call-ID: a84b4c76e66710\r\n"
      + "CSeq: 63104 OPTIONS\r\n"
      + "Contact: <sip:alice@pc33.atlanta.com>\r\n"
      + "Accept: application/sdp\r\n"
      + "Content-Length: 0\r\n"
      + "\r\n";
  private SipRequest msg;
  private final SipMessageManager manager = new RfcSipMessageManager();

  @BeforeEach
  public void setup() {
    RawMessage msg = RawMessage.create("OPTIONS sip:carol@chicago.com SIP/2.0");
    msg.addHeader(new RawHeader("Via", "SIP/2.0/UDP pc33.atlanta.com;branch=z9hG4bKhjhs8ass877"));
    msg.addHeader(new RawHeader("Max-Forwards", "70"));
    msg.addHeader(new RawHeader("To", "<sip:carol@chicago.com>"));
    msg.addHeader(new RawHeader("From", "Alice <sip:alice@atlanta.com>;tag=1928301774"));
    msg.addHeader(new RawHeader("Call-ID", "a84b4c76e66710"));
    msg.addHeader(new RawHeader("CSeq", "63104 OPTIONS"));
    msg.addHeader(new RawHeader("Contact", "<sip:alice@pc33.atlanta.com>"));
    msg.addHeader(new RawHeader("Accept", "application/sdp"));
    msg.addHeader(new RawHeader("Content-Length", "0"));
    this.msg = (SipRequest) manager.convert(msg);
  }

  @Test
  public void test() {
    SipRequestSerializer serializer = new SipRequestSerializer(new RfcSerializerManagerBuilder().build());
    assertEquals(result, serializer.serialize(msg));
  }

}
