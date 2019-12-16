/**
 * 
 */
package com.jive.sip.message.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.api.NameAddr;
import com.jive.sip.message.api.SipHeaderDefinition;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.TokenParameterValue;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.processor.rfc3261.message.impl.SingleHeaderDefinition;
import com.jive.sip.processor.rfc3261.parsing.parsers.headers.NameAddrParser;
import com.jive.sip.processor.uri.RawUri;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class SingleHeaderDefintionTest {
  private SipHeaderDefinition<NameAddr> from;
  private List<RawHeader> headers;

  @BeforeEach
  public void setup() {
    this.from = SingleHeaderDefinition.create(new NameAddrParser(), "From", 'f');
    this.headers = Lists.newLinkedList();
  }

  @Test
  public void testLongName() {
    this.headers.add(new RawHeader("From", "\"A. G. Bell\" <sip:agb@bell-telephone.com> ;tag=a48s"));
    NameAddr name = this.from.parse(this.headers);
    assertEquals("A. G. Bell", name.getName().get());
    assertEquals(new RawUri("sip", "agb@bell-telephone.com"), name.getAddress());
    assertEquals(DefaultParameters.from(Lists.newArrayList(new RawParameter("tag", new TokenParameterValue("a48s")))), name.getParameters().get());
  }

  @Test
  public void testCompactName() {
    this.headers.add(new RawHeader("f", "Anonymous <sip:c8oqz84zk7z@privacy.org>;tag=hyh8"));
    NameAddr name = this.from.parse(this.headers);
    assertEquals("Anonymous", name.getName().get());
    assertEquals(new RawUri("sip", "c8oqz84zk7z@privacy.org"), name.getAddress());
    assertEquals(DefaultParameters.from(Lists.newArrayList(new RawParameter("tag", new TokenParameterValue("hyh8")))), name.getParameters().get());
  }

}
