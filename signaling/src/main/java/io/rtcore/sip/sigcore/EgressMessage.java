package io.rtcore.sip.sigcore;

public interface EgressMessage {

  // The target egress namespace
  String egressNamespace();

  // The target egress type
  String egressType();

  // egress argument
  TypedValue argument();

}
