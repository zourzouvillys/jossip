package io.rtcore.gateway.api;

import java.util.Map;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ValueNode;

import io.rtcore.sip.common.SipHeaders;
import io.rtcore.sip.common.iana.SipMethodId;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonInclude(value = Include.NON_DEFAULT)
@JsonSerialize
@JsonDeserialize(builder = ImmutableNICTRequest.Builder.class)
public interface NICTRequest extends WithNICTRequest {

  /**
   * the route to use for sending this request.
   *
   * if not set, derived from the message or the segment being used.
   *
   */

  Optional<SipRoutePayload> route();

  /**
   * the SIP method for this NICT.
   */

  @JsonDeserialize(using = SipMethodDeserializer.class)
  @JsonSerialize(using = SipMethodSerializer.class)
  SipMethodId method();

  /**
   * the R-URI, if not specified will be derived from the next-hop.
   */

  Optional<String> uri();

  /**
   * the sip headers to send with the request.
   */

  @JsonDeserialize(using = SipHeaderDeserializer.class)
  @JsonSerialize(using = SipHeaderSerializer.class)
  @Value.Default
  default SipHeaders headers() {
    return SipHeaders.emptyHeaders();
  }

  /**
   * the request body, if one is needed.
   */

  Optional<String> body();

  /**
   * properties related to this request, implementation specific.
   */

  Map<String, ValueNode> properties();

}
