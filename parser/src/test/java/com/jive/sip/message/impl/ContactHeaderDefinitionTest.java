package com.jive.sip.message.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.api.ContactSet;
import com.jive.sip.processor.rfc3261.message.impl.ContactHeaderDefinition;

public class ContactHeaderDefinitionTest
{
  final ContactHeaderDefinition def = new ContactHeaderDefinition();
  
  @Test
  public void testStar()
  {
    final List<RawHeader> headers = Lists.newLinkedList();
    headers.add(new RawHeader("Contact", "*"));
    final ContactSet set = def.parse(headers);
    assertTrue(set.isStar());
  }

  @Test
  public void testSingleEntry()
  {
    final List<RawHeader> headers = Lists.newLinkedList();
    headers.add(new RawHeader("Contact", "sip:bob"));
    final ContactSet set = def.parse(headers);
    assertFalse(set.isStar());
    assertEquals(1, set.size());
  }

  @Test
  public void testSingleQuotedEntry()
  {
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
  public void testMultiOnSingleLine1()
  {
    final List<RawHeader> headers = Lists.newLinkedList();
    headers.add(new RawHeader("Contact", "<sip:alice>, <sip:bob>"));
    final ContactSet set = def.parse(headers);
    assertFalse(set.isStar());
    assertEquals(2, set.size());
  }

  @Test
  public void testMultiOnSingleLine2()
  {
    final List<RawHeader> headers = Lists.newLinkedList();
    headers.add(new RawHeader("Contact", "<sip:alice>;xxx, <tel:+44>;ccc"));
    final ContactSet set = def.parse(headers);
    assertFalse(set.isStar());
    assertEquals(2, set.size());
  }

}
