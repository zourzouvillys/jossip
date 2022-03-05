package io.rtcore.sip.common.iana;

import java.net.URI;
import java.util.Set;

public final class UnknownSipHeaderId implements SipHeaderId {

  private String key;

  UnknownSipHeaderId(String key) {
    this.key = key;
  }

  @Override
  public URI headerId() {
    return null;
  }

  @Override
  public Set<String> headerNames() {
    return Set.of(this.key);
  }

  @Override
  public String prettyName() {
    return this.key;
  }

  public static UnknownSipHeaderId of(String key) {
    return new UnknownSipHeaderId(key);
  }

}
