/**
 * 
 */
package io.rtcore.sip.message.message.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.message.api.SipHeaderDefinition;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.processor.rfc3261.message.impl.SingleHeaderDefinition;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.NameAddrParser;
import io.rtcore.sip.message.processor.uri.RawUri;

/**
 * 
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
    assertEquals(RawUri.of("sip", "agb@bell-telephone.com"), name.address());
    assertEquals(DefaultParameters.from(Lists.newArrayList(new RawParameter("tag", new TokenParameterValue("a48s")))), name.getParameters().get());
  }

  @Test
  public void testCompactName() {
    this.headers.add(new RawHeader("f", "Anonymous <sip:c8oqz84zk7z@privacy.org>;tag=hyh8"));
    NameAddr name = this.from.parse(this.headers);
    assertEquals("Anonymous", name.getName().get());
    assertEquals(RawUri.of("sip", "c8oqz84zk7z@privacy.org"), name.address());
    assertEquals(DefaultParameters.from(Lists.newArrayList(new RawParameter("tag", new TokenParameterValue("hyh8")))), name.getParameters().get());
  }

}
