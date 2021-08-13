/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.auth.headers.Authorization;
import io.rtcore.sip.message.parameters.api.QuotedStringParameterValue;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManagerBuilder;

/**
 * 
 * 
 */
public class AuthorizationSerializerTest {
  private RfcSerializer<Authorization> serializer;

  @BeforeEach
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
