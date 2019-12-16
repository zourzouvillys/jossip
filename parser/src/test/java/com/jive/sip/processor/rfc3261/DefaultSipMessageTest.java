/**
 * 
 */
package com.jive.sip.processor.rfc3261;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.SipMessage;
import com.jive.sip.message.SipRequest;
import com.jive.sip.message.api.ContactSet;
import com.jive.sip.message.api.NameAddr;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.processor.uri.RawUri;
import com.jive.sip.uri.SipUri;

/**
 * @author Jeff Hutchins {@code <jhutchins@getjive.com>}
 * 
 */
public class DefaultSipMessageTest {
  @Test
  public void getContacts() {
    SipMessage message;
    Optional<ContactSet> contacts;

    // No contacts
    message = new DefaultSipRequest(null, SipMethod.INVITE, new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    assertFalse(message.contacts().isPresent());

    final String value = "getjive.com";
    message =
      new DefaultSipRequest(
        null,
        SipMethod.INVITE,
        new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)),
        "2.0",
        Lists.newArrayList(new RawHeader("Contact", "sip:" + value)),
        null);
    contacts = message.contacts();
    assertTrue(contacts.isPresent());
    assertFalse(contacts.get().isStar());
    assertEquals(1, contacts.get().size());
    for (final NameAddr addr : contacts.get()) {
      assertEquals(new NameAddr(RawUri.of("sip", value)), addr);
    }

    message =
      new DefaultSipRequest(
        null,
        SipMethod.INVITE,
        new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)),
        "2.0",
        Lists.newArrayList(new RawHeader("Contact", "*")),
        null);
    contacts = message.contacts();
    assertTrue(contacts.isPresent());
    assertTrue(contacts.get().isStar());
  }

  @Test
  public void parseExpires() {
    UnsignedInteger maxExpires = UnsignedInteger.valueOf(2147483647);
    SipRequest message =
      new DefaultSipRequest(
        null,
        SipMethod.INVITE,
        new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)),
        "2.0",
        Lists.newArrayList(new RawHeader("Expires", maxExpires.toString())),
        null);

    Optional<UnsignedInteger> expires = message.expires();

    assertTrue(expires.isPresent());
    assertEquals(maxExpires, expires.get());
  }
}
