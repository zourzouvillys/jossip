package io.rtcore.gateway.api;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
@JsonInclude(value = Include.NON_DEFAULT)
@JsonSerialize
@JsonDeserialize(builder = ImmutableSipRoutePayload.Builder.class)
@JsonAutoDetect(fieldVisibility = Visibility.PUBLIC_ONLY)
public interface SipRoutePayload {

  /**
   * a transport uri which is the outbound proxy to send via, e.g udp:1.2.3.4:5060.
   */

  String nextHop();

}
