package io.rtcore.sip.message.message.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.message.api.ContactSet;
import io.rtcore.sip.message.processor.rfc3261.message.impl.ContactHeaderDefinition;

public class ContactHeaderDefinitionTest {
  final ContactHeaderDefinition def = new ContactHeaderDefinition();

  @Test
  public void testStar() {
    final List<RawHeader> headers = Lists.newLinkedList();
    headers.add(new RawHeader("Contact", "*"));
    final ContactSet set = def.parse(headers);
    assertTrue(set.isStar());
  }

  @Test
  public void testSingleEntry() {
    final List<RawHeader> headers = Lists.newLinkedList();
    headers.add(new RawHeader("Contact", "sip:bob"));
    final ContactSet set = def.parse(headers);
    assertFalse(set.isStar());
    assertEquals(1, set.size());
  }

  @Test
  public void testSingleQuotedEntry() {
    final List<RawHeader> headers = Lists.newLinkedList();
    headers
      .add(new RawHeader(
        "Contact",
        "<sip:01369ce3423ab364b8000100620002@192.168.1.227>;methods=\"INVITE, ACK, BYE, CANCEL, OPTIONS, INFO, MESSAGE, SUBSCRIBE, NOTIFY, PRACK, UPDATE, REFER\""));
    final ContactSet set = def.parse(headers);
    assertFalse(set.isStar());
    assertEquals(1, set.size());
  }

  @Test
  public void testMultiOnSingleLine1() {
    final List<RawHeader> headers = Lists.newLinkedList();
    headers.add(new RawHeader("Contact", "<sip:alice>, <sip:bob>"));
    final ContactSet set = def.parse(headers);
    assertFalse(set.isStar());
    assertEquals(2, set.size());
  }

  @Test
  public void testMultiOnSingleLine2() {
    final List<RawHeader> headers = Lists.newLinkedList();
    headers.add(new RawHeader("Contact", "<sip:alice>;xxx, <tel:+44>;ccc"));
    final ContactSet set = def.parse(headers);
    assertFalse(set.isStar());
    assertEquals(2, set.size());
  }

}
