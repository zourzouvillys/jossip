package io.rtcore.sip.frame;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.SipInitialLine;
import io.rtcore.sip.common.iana.SipMethodId;

public interface SipFrame {

  /**
   * the initial SIP header line. this will always be a request or response.
   */

  SipInitialLine initialLine();

  /**
   * each of the header lines.
   */

  List<SipHeaderLine> headerLines();

  /**
   * the body, if content-length is not 0.
   */

  Optional<String> body();

  static SipRequestFrame of(final SipMethodId method, final URI uri, final Iterable<? extends SipHeaderLine> headerLines) {
    return of(method, uri, headerLines, Optional.empty());
  }

  static SipRequestFrame of(final SipMethodId method, final URI uri, final Iterable<? extends SipHeaderLine> headerLines, final Optional<String> body) {
    return ImmutableSipRequestFrame.of(SipInitialLine.of(method, uri), headerLines, body);
  }

  static SipRequestFrame of(final SipMethodId method, final URI uri, final Iterable<? extends SipHeaderLine> headerLines, final String body) {
    return ImmutableSipRequestFrame.of(SipInitialLine.of(method, uri), headerLines, Optional.ofNullable(body));
  }

  static SipFrame of(final SipInitialLine initialLine, final Iterable<? extends SipHeaderLine> headerLines) {
    return of(initialLine, headerLines, Optional.empty());
  }

  static SipFrame of(final SipInitialLine initialLine, final Iterable<? extends SipHeaderLine> headerLines, final String body) {
    return of(initialLine, headerLines, Optional.of(body));
  }

  static SipFrame of(final SipInitialLine initialLine, final Iterable<? extends SipHeaderLine> headerLines, final Optional<String> body) {
    if (initialLine instanceof final SipInitialLine.RequestLine req) {
      return ImmutableSipRequestFrame.of(req, headerLines, body);
    }
    if (initialLine instanceof final SipInitialLine.ResponseLine res) {
      return ImmutableSipResponseFrame.of(res, headerLines, body);
    }
    throw new IllegalArgumentException();
  }

}
