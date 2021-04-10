package io.rtcore.sip.sigcore;

import java.time.Duration;

public interface ExpirationSpec {

  public enum ExpireMode {
    NONE,
    AFTER_WRITE,
    AFTER_INVOKE,
  }

  ExpireMode mode();

  Duration expireAfter();

}
