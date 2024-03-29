package io.rtcore.sip.frame;

import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.SipInitialLine;
import io.rtcore.sip.common.iana.SipStatusCodes;

@Value.Immutable(builder = false)
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class }, attributeBuilderDetection = true, deepImmutablesDetection = true,
    strictBuilder = true)
public sealed interface SipResponseFrame extends SipFrame, WithSipResponseFrame permits ImmutableSipResponseFrame {

  /**
   * the initial SIP header line. this will always be a request or response.
   */

  @Override
  @Value.Parameter
  SipInitialLine.ResponseLine initialLine();

  /**
   * each of the header lines.
   */

  @Override
  @Value.Parameter
  List<SipHeaderLine> headerLines();

  /**
   * the body, if content-length is not 0.
   */

  @Override
  @Value.Parameter
  Optional<String> body();

  static SipResponseFrame of(final SipStatusCodes status) {
    return ImmutableSipResponseFrame.of(SipInitialLine.of(status), List.of(), Optional.empty());
  }

  static SipResponseFrame of(final SipStatusCodes status, final Iterable<SipHeaderLine> headerLines) {
    return ImmutableSipResponseFrame.of(SipInitialLine.of(status), headerLines, Optional.empty());
  }

  static SipResponseFrame of(final SipStatusCodes status, final Iterable<SipHeaderLine> headerLines, final String body) {
    return ImmutableSipResponseFrame.of(SipInitialLine.of(status), headerLines, Optional.ofNullable(body).filter(val -> !val.isEmpty()));
  }

  static SipResponseFrame of(final SipStatusCodes status, final Iterable<SipHeaderLine> headerLines, final Optional<String> body) {
    return ImmutableSipResponseFrame.of(SipInitialLine.of(status), headerLines, body.filter(val -> !val.isEmpty()));
  }

}
