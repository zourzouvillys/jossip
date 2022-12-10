package io.rtcore.sip.channels.api;

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

  static SipRequestFrame of(SipMethodId method, URI uri, Iterable<? extends SipHeaderLine> headerLines) {
    return of(method, uri, headerLines, Optional.empty());
  }

  static SipRequestFrame of(SipMethodId method, URI uri, Iterable<? extends SipHeaderLine> headerLines, Optional<String> body) {
    return ImmutableSipRequestFrame.of(SipInitialLine.of(method, uri), headerLines, body);
  }

  static SipRequestFrame of(SipMethodId method, URI uri, Iterable<? extends SipHeaderLine> headerLines, String body) {
    return ImmutableSipRequestFrame.of(SipInitialLine.of(method, uri), headerLines, Optional.ofNullable(body));
  }

  static SipFrame of(SipInitialLine initialLine, Iterable<? extends SipHeaderLine> headerLines) {
    return of(initialLine, headerLines, Optional.empty());
  }

  static SipFrame of(SipInitialLine initialLine, Iterable<? extends SipHeaderLine> headerLines, String body) {
    return of(initialLine, headerLines, Optional.of(body));
  }

  static SipFrame of(SipInitialLine initialLine, Iterable<? extends SipHeaderLine> headerLines, Optional<String> body) {
    if (initialLine instanceof SipInitialLine.RequestLine req) {
      return ImmutableSipRequestFrame.of(req, headerLines, body);
    }
    else if (initialLine instanceof SipInitialLine.ResponseLine res) {
      return ImmutableSipResponseFrame.of(res, headerLines, body);
    }
    throw new IllegalArgumentException();
  }

}
