package io.rtcore.sip.common;

import java.util.Optional;

import org.immutables.value.Value;

import io.rtcore.sip.common.iana.SipHeaderId;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.common.iana.UnknownSipHeaderId;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public interface SipHeaderLine extends WithSipHeaderLine {

  /**
   * the raw header name, stored with potential in the case form received.
   */

  @Value.Parameter
  String headerName();

  /**
   * the raw value. if it originally spanned multiple lines (using CRLF LWS), then it will include a
   * single space instead.
   */

  @Value.Parameter
  String headerValues();

  /**
   * if the field has a standard well-known id from {@link StandardSipHeaders} it will be set here.
   */

  @Value.Derived
  default Optional<StandardSipHeaders> knownHeaderId() {
    return Optional.ofNullable(StandardSipHeaders.fromString(headerName()));
  }

  @Value.Derived
  default SipHeaderId headerId() {
    return Optional.<SipHeaderId>ofNullable(StandardSipHeaders.fromString(headerName())).orElseGet(() -> UnknownSipHeaderId.of(headerName()));
  }

  /**
   * 
   */

  static SipHeaderLine of(StandardSipHeaders name, String value) {
    return ImmutableSipHeaderLine.of(name.prettyName(), value);
  }

  static SipHeaderLine of(String name, String value) {
    return ImmutableSipHeaderLine.of(name, value);
  }

}
