package io.rtcore.sip.channels.internal;

import io.rtcore.sip.channels.internal.ManagedSipUdpSocketRegistry.ProviderNotFoundException;

public interface ManagedSipUdpSocketProvider {

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
   * create a new builder instance.
   */

  ManagedSipUdpSocketBuilder<?> builder();

  /**
   *
   */

  static ManagedSipUdpSocketProvider provider() {
    final ManagedSipUdpSocketProvider provider = ManagedSipUdpSocketRegistry.getDefaultRegistry().provider();
    if (provider == null) {
      throw new ProviderNotFoundException("No functional udp socket server provider found.");
    }
    return provider;
  }

}
