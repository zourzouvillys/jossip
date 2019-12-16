/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.net.HostAndPort;
import com.jive.sip.base.api.Token;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializer;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManagerBuilder;
import com.jive.sip.uri.SipUri;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class SipUriSerializerTest {

  private RfcSerializer<SipUri> serializer;

  @BeforeEach
  public void setup() {
    this.serializer = new SipUriSerializer(new RfcSerializerManagerBuilder().build());
  }

  @Test
  public void test() throws IOException {
    final SipUri uri = SipUri.fromUserAndHost("theo", HostAndPort.fromParts("test.com", 5060));
    final StringWriter w = new StringWriter();
    this.serializer.serialize(w, uri);
    assertEquals("sip:theo@test.com:5060", w.toString());
  }

  @Test
  public void testNoPort() throws IOException {
    final SipUri uri = SipUri.fromUserAndHost("theo", HostAndPort.fromString("test.com"));
    final StringWriter w = new StringWriter();
    this.serializer.serialize(w, uri);
    assertEquals("sip:theo@test.com", w.toString());
  }

  @Test
  public void testNoUser() throws IOException {
    final SipUri uri = new SipUri(HostAndPort.fromString("test.com"));
    final StringWriter w = new StringWriter();
    this.serializer.serialize(w, uri);
    assertEquals("sip:test.com", w.toString());
  }

  @Test
  public void testWithParams() throws IOException {
    final SipUri uri = new SipUri(HostAndPort.fromString("test.com")).withParameter(Token.from("xxx"));
    final StringWriter w = new StringWriter();
    this.serializer.serialize(w, uri);
    assertEquals("sip:test.com;xxx", w.toString());
  }

  @Test
  public void testWithMultipleParams() throws IOException {
    final SipUri uri = new SipUri(HostAndPort.fromString("test.com")).withParameter(Token.from("xxx"), Token.from("yyy")).withParameter(Token.from("ob"));
    final StringWriter w = new StringWriter();
    this.serializer.serialize(w, uri);
    assertEquals("sip:test.com;xxx=yyy;ob", w.toString());
  }
}
