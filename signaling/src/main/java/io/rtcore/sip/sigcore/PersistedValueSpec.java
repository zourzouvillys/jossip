package io.rtcore.sip.sigcore;

import java.util.Optional;

public interface PersistedValueSpec {
  
  String stateName();

  Optional<ExpirationSpec> expirationSpec();

  String typeTypeName();
  
}
