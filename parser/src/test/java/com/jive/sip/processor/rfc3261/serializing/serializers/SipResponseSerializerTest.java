/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.jive.sip.base.api.RawHeader;
import com.jive.sip.base.api.RawMessage;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.processor.rfc3261.RfcSipMessageManager;
import com.jive.sip.processor.rfc3261.SipMessageManager;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManagerBuilder;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class SipResponseSerializerTest {
  private final String result =
    "SIP/2.0 200 OK\r\n"
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
  private SipResponse msg;
  private final SipMessageManager manager = new RfcSipMessageManager();

  @Before
  public void setup() {
    final RawMessage msg = RawMessage.create("SIP/2.0 200 OK");
    msg.addHeader(new RawHeader("Via", "SIP/2.0/UDP pc33.atlanta.com;branch=z9hG4bKhjhs8ass877"));
    msg.addHeader(new RawHeader("Max-Forwards", "70"));
    msg.addHeader(new RawHeader("To", "<sip:carol@chicago.com>"));
    msg.addHeader(new RawHeader("From", "Alice <sip:alice@atlanta.com>;tag=1928301774"));
    msg.addHeader(new RawHeader("Call-ID", "a84b4c76e66710"));
    msg.addHeader(new RawHeader("CSeq", "63104 OPTIONS"));
    msg.addHeader(new RawHeader("Contact", "<sip:alice@pc33.atlanta.com>"));
    msg.addHeader(new RawHeader("Accept", "application/sdp"));
    msg.addHeader(new RawHeader("Content-Length", "0"));
    this.msg = (SipResponse) this.manager.convert(msg);
  }

  @Test
  public void test() {
    final SipResponseSerializer serializer = new SipResponseSerializer(new RfcSerializerManagerBuilder().build());
    assertEquals(this.result, serializer.serialize(this.msg));
  }

}
