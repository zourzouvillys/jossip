package io.rtcore.sip.frame;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

import io.rtcore.sip.common.ImmutableRequestLine;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.SipHeaders;
import io.rtcore.sip.common.SipInitialLine;
import io.rtcore.sip.common.iana.SipMethodId;


@Value.Immutable(builder = false)
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class }, attributeBuilderDetection = true, deepImmutablesDetection = true)
public interface SipRequestFrame extends SipFrame, WithSipRequestFrame {

  /**
   * the initial SIP header line. this will always be a request or response.
   */

  @Override
  @Value.Parameter
  SipInitialLine.RequestLine initialLine();

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

  static SipRequestFrame of(final SipMethodId method, final String ruri) {
    return ImmutableSipRequestFrame.of(ImmutableRequestLine.of(method, URI.create(ruri)), List.of(), Optional.empty());
  }

  static SipRequestFrame of(final SipMethodId method, final URI ruri, final SipHeaders headers) {
    return ImmutableSipRequestFrame.of(ImmutableRequestLine.of(method, ruri), List.copyOf(headers.lines()), Optional.empty());
  }

  static SipRequestFrame of(final SipMethodId method, final URI ruri, final Collection<SipHeaderLine> headers) {
    return ImmutableSipRequestFrame.of(ImmutableRequestLine.of(method, ruri), List.copyOf(headers), Optional.empty());
  }

}
