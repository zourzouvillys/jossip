package io.rtcore.sip.common.iana;

import java.net.URI;
import java.util.Set;

public interface SipHeaderId {

  /**
   * a unique normalized header URL identifier.
   */

  URI headerId();

  /**
   * all of the possible names this header matches, e.g ['f', 'From']
   */

  Set<String> headerNames();

  /**
   * the name for displaying (non-compact form), e.g 'Record-Route'.
   */

  String prettyName();

}
