package io.rtcore.gateway.api;

import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.rtcore.sip.common.SipHeaders;
import io.rtcore.sip.common.iana.SipStatusCodes;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonInclude(value = Include.NON_DEFAULT)
@JsonSerialize
@JsonDeserialize(builder = ImmutableSipResponsePayload.Builder.class)
public interface SipResponsePayload {

  Optional<String> flowId();

  int statusCode();

  Optional<String> reasonPhrase();

  @JsonDeserialize(using = SipHeaderDeserializer.class)
  @JsonSerialize(using = SipHeaderSerializer.class)
  @Value.Default
  default SipHeaders headers() {
    return SipHeaders.emptyHeaders();
  }

  /**
   *
   */

  Optional<String> body();

  /**
   * RFC 3261 encodinug of SIP response.
   *
   * @param payload
   * @return
   */

  static String toString(final SipResponsePayload payload) {

    final StringBuilder sb = new StringBuilder();

    sb.append("SIP/2.0 ")
      .append(payload.statusCode())
      .append(" ")
      .append(payload.reasonPhrase().orElse(SipStatusCodes.defaultReason(payload.statusCode())))
      .append("\r\n");

    payload.headers()
      .lines()
      .forEach(line -> sb.append(line.headerId().prettyName()).append(": ").append(line.headerValues()).append("\r\n"));

    sb.append("\r\n");

    payload.body().ifPresent(body -> sb.append(body));

    return sb.toString();

  }

}
