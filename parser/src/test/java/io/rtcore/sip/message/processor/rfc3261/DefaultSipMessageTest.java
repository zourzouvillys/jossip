/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.message.SipMessage;
import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.api.ContactSet;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.processor.rfc3261.DefaultSipRequest;
import io.rtcore.sip.message.processor.uri.RawUri;
import io.rtcore.sip.message.uri.SipUri;

/**
 * 
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
