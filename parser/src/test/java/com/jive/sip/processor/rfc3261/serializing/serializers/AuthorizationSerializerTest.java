/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.jive.sip.auth.headers.Authorization;
import com.jive.sip.parameters.api.QuotedStringParameterValue;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializer;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManagerBuilder;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class AuthorizationSerializerTest {
  private RfcSerializer<Authorization> serializer;

  @Before
  public void setup() {
    serializer = new AuthorizationSerializer(new RfcSerializerManagerBuilder().build());
  }

  @Test
  public void test() {
    Authorization auth =
      new Authorization(
        "Digest",
        DefaultParameters.from(Lists.newArrayList(new RawParameter("username", new QuotedStringParameterValue("Alice")),
          new RawParameter("realm", new QuotedStringParameterValue("atlanta.com")),
          new RawParameter("nonce", new QuotedStringParameterValue("84a4cc6f3082121f32b42a2187831a9e")),
          new RawParameter("response", new QuotedStringParameterValue("7587245234b3434cc3412213e5f113a5432")))));
    assertEquals("Digest username=\"Alice\", realm=\"atlanta.com\", "
      +
      "nonce=\"84a4cc6f3082121f32b42a2187831a9e\", response=\"7587245234b3434cc3412213e5f113a5432\"",
      serializer.serialize(auth));
  }

}
