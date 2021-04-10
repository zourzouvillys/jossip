package io.rtcore.sip.sigcore;

import java.time.Duration;
import java.util.Optional;

public interface FromInvocation {

  Optional<String> id();

  Optional<Duration> delay();

  // The target function to invoke
  Address target();

  // The invocation argument (aka the message sent to the target function)
  TypedValue argument();

}
