package io.rtcore.sip.channels.connection;

import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.SipInitialLine;
import io.rtcore.sip.common.iana.SipStatusCodes;

@Value.Immutable(builder = false)
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class }, attributeBuilderDetection = true, deepImmutablesDetection = true,
    strictBuilder = true)
public interface SipResponseFrame extends SipFrame, WithSipResponseFrame {

  /**
   * the initial SIP header line. this will always be a request or response.
   */

  @Value.Parameter
  SipInitialLine.ResponseLine initialLine();

  /**
   * each of the header lines.
   */

  @Value.Parameter
  List<SipHeaderLine> headerLines();

  /**
   * the body, if content-length is not 0.
   */

  @Value.Parameter
  Optional<String> body();

  static SipResponseFrame of(SipStatusCodes status) {
    return ImmutableSipResponseFrame.of(SipInitialLine.of(status), List.of(), Optional.empty());
  }

  static SipResponseFrame of(SipStatusCodes status, Iterable<SipHeaderLine> headerLines) {
    return ImmutableSipResponseFrame.of(SipInitialLine.of(status), headerLines, Optional.empty());
  }

  static SipResponseFrame of(SipStatusCodes status, Iterable<SipHeaderLine> headerLines, String body) {
    return ImmutableSipResponseFrame.of(SipInitialLine.of(status), headerLines, Optional.ofNullable(body).filter(val -> !val.isEmpty()));
  }

}
