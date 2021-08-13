package io.rtcore.sip.channels;

import java.net.SocketAddress;
import java.net.URI;
import java.util.concurrent.Flow;

import io.rtcore.sip.channels.SipNameResolver.ResolutionResult;

public interface SipNameResolver extends Flow.Publisher<ResolutionResult> {

  /**
   * a node within the resolution tree, which could be an {@link Address} or {@link AddressGroup}.
   */

  interface AddressNode extends Attributed {
  }

  /**
   * a single logical address. there may be multiple {@link SocketAddress} entries, in which case
   * the addresses are all identical, for example when there is A and AAAA entries for a hostname
   * with DNS resolution.
   */

  interface Address extends AddressNode, Flow.Publisher<SocketAddress> {
  }

  /**
   * each of the entries for this group. note that groups may contain other groups.
   *
   * we don't return as a flat list, as this allows deciding which groups to resolve on demand,
   * without needing to wait for the full set.
   *
   */

  interface AddressGroup extends AddressNode, Flow.Publisher<AddressNode> {
  }

  /**
   * list of {@link AddressGroup} instances.
   *
   * how each entry maps to actual connections or request routing is dependent on the load balancer
   * in use. this may be as simple as picking the first which works and sticking to it, or it could
   * be something more complex such as attempting connections in parallel until one works. a client
   * could also attempt to keep multiple connections open as backups.
   *
   */

  interface ResolutionResult extends Flow.Publisher<AddressNode>, Attributed {
  }

  /**
   *
   */

  interface Factory {

    /**
     * Creates a {@link SipNameResolver} for the given target URI, or {@code null} if the given URI
     * cannot be resolved by this factory. The decision should be solely based on the scheme of the
     * URI.
     */

    SipNameResolver newNameResolver(URI targetUri);

    /**
     * Returns the default scheme, which will be used to construct a URI when new channel is given
     * an authority string instead of a compliant URI.
     */

    String getDefaultScheme();

  }

  public interface Provider extends Factory {

    /**
     * Whether this provider is available for use, taking the current environment into
     * consideration. If {@code false}, no other methods are safe to be called.
     */

    boolean isAvailable();

    /**
     * A priority, from 0 to 10 that this provider should be used, taking the current environment
     * into consideration. 5 should be considered the default, and then tweaked based on environment
     * detection. A priority of 0 does not imply that the provider wouldn't work; just that it
     * should be last in line.
     */

    int priority();

  }

}
