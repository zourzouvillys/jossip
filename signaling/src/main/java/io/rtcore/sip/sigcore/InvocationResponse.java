package io.rtcore.sip.sigcore;

import java.util.List;

public interface InvocationResponse extends FromFunction {

  List<PersistedValueMutation> stateMutations();

  List<FromInvocation> outgoingMessages();

  List<EgressMessage> outgoingEgresses();

}
