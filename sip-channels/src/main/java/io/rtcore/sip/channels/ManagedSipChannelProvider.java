package io.rtcore.sip.channels;

public interface ManagedSipChannelProvider {

  /**
   * A priority, from 0 to 10 that this provider should be used, taking the current environment into
   * consideration. 5 should be considered the default, and then tweaked based on environment
   * detection. A priority of 0 does not imply that the provider wouldn't work; just that it should
   * be last in line.
   */

  default int priority() {
    return 0;
  }

  /**
   * Whether this provider is available for use, taking the current environment into consideration.
   * If {@code false}, no other methods are safe to be called.
   */

  default boolean isAvailable() {
    return true;
  }

  /**
   *
   */

  ManagedSipChannelBuilder<?> newChannelBuilder(String target, SipChannelCredentials creds);

}
