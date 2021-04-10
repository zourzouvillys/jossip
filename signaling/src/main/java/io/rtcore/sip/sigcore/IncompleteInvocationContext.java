package io.rtcore.sip.sigcore;

import java.util.List;

public interface IncompleteInvocationContext extends FromFunction {

  List<PersistedValueSpec> missingValues();

}
