package io.rtcore.sip.channels.connection;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

import io.rtcore.sip.common.ImmutableRequestLine;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.SipHeaders;
import io.rtcore.sip.common.SipInitialLine;
import io.rtcore.sip.common.iana.SipMethods;

@Value.Immutable(builder = false)
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class }, attributeBuilderDetection = true, deepImmutablesDetection = true)
public interface SipRequestFrame extends SipFrame, WithSipRequestFrame {
  
  /**
   * the initial SIP header line. this will always be a request or response.
   */

  @Value.Parameter
  SipInitialLine.RequestLine initialLine();

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

  static SipRequestFrame of(SipMethods method, URI ruri, SipHeaders headers) {
    return ImmutableSipRequestFrame.of(ImmutableRequestLine.of(method, ruri), List.copyOf(headers.lines()), Optional.empty());
  }

  static SipRequestFrame of(SipMethods method, URI ruri, Collection<SipHeaderLine> headers) {
    return ImmutableSipRequestFrame.of(ImmutableRequestLine.of(method, ruri), List.copyOf(headers), Optional.empty());
  }

}
