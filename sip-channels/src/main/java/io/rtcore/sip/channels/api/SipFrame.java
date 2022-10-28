package io.rtcore.sip.channels.api;

import java.util.List;
import java.util.Optional;

import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.SipInitialLine;

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

}
