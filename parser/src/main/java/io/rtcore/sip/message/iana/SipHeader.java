package io.rtcore.sip.message.iana;

import java.net.URI;
import java.util.Set;

public interface SipHeader {

  /**
   * a unique normalized header URL identifier.
   */

  URI headerId();

  Set<String> headerNames();

  /**
   * the name for displaying, e.g 'Record-Route'.
   */

  String prettyName();

}
