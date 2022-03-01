package io.rtcore.sip.channels;

import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Flow;

import com.google.common.base.Preconditions;

import io.rtcore.sip.channels.SipNameResolver.ResolutionResult;
import io.rtcore.sip.common.HostPort;

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
     * Creates a {@link SipNameResolver} for the given target {@link URI}, or {@code null} if the
     * given URI cannot be resolved by this factory.
     */

    SipNameResolver newNameResolver(URI targetUri);

    /**
     * Returns the default scheme, which will be used to construct a URI when a host and/or hostPort
     * is given to resolvr.
     */

    String defaultScheme();

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

  public final class Registry {

    private static final class PriorityAccessor implements ServiceProviders.PriorityAccessor<Provider> {

      @Override
      public boolean isAvailable(final Provider provider) {
        return provider.isAvailable();
      }

      @Override
      public int getPriority(final Provider provider) {
        return provider.priority();
      }

    }

    private static Registry instance;

    private final LinkedHashSet<Provider> allProviders = new LinkedHashSet<>();

    /** Immutable, sorted version of {@code allProviders}. Is replaced instead of mutating. */
    private List<Provider> effectiveProviders = Collections.emptyList();

    private Registry() {
    }

    public static synchronized Registry defaultRegistry() {

      if (instance == null) {

        final List<Provider> providerList =
            ServiceProviders.loadAll(
              SipNameResolver.Provider.class,
              List.of(),
              SipNameResolver.Provider.class.getClassLoader(),
              new PriorityAccessor());

        instance = new Registry();

        for (final Provider provider : providerList) {
          if (provider.isAvailable()) {
            instance.addProvider(provider);
          }
        }

        instance.refreshProviders();

      }
      return instance;
    }

    private synchronized void refreshProviders() {
      final List<Provider> providers = new ArrayList<>(this.allProviders);
      // Sort descending based on priority.
      // sort() must be stable, as we prefer first-registered providers
      Collections.sort(providers, Collections.reverseOrder((o1, o2) -> o1.priority() - o2.priority()));
      this.effectiveProviders = Collections.unmodifiableList(providers);
    }

    private synchronized void addProvider(final Provider provider) {
      Preconditions.checkArgument(provider.isAvailable(), "isAvailable() returned false");
      this.allProviders.add(provider);
    }

    synchronized List<Provider> providers() {
      return this.effectiveProviders;
    }

    public SipNameResolver newNameResolver(final URI targetUri) {

      final List<Provider> providers = this.providers();

      if (providers.isEmpty()) {
        throw new ProviderNotFoundException("No functional udp socket service provider found.");
      }

      final StringBuilder error = new StringBuilder();

      for (final Provider provider : this.providers()) {

        final SipNameResolver resolver = provider.newNameResolver(targetUri);

        if (resolver != null) {
          return resolver;
        }

        error.append("; ");
        error.append(provider.getClass().getName());

      }

      throw new ProviderNotFoundException(error.substring(2));

    }

    public SipNameResolver newNameResolver(final HostPort target) {

      final List<Provider> providers = this.providers();

      if (providers.isEmpty()) {
        throw new ProviderNotFoundException("No functional name resolver service provider found.");
      }

      final StringBuilder error = new StringBuilder();

      for (final Provider provider : this.providers()) {

        try {

          final SipNameResolver resolver = provider.newNameResolver(new URI(provider.defaultScheme(), target.toUriString(), null));

          if (resolver != null) {
            return resolver;
          }

        }
        catch (final URISyntaxException e) {
        }

        error.append("; ");
        error.append(provider.getClass().getName());

      }

      throw new ProviderNotFoundException(error.substring(2));

    }

    /**
     * Thrown when no suitable providers objects can be found.
     */

    public static final class ProviderNotFoundException extends RuntimeException {

      private static final long serialVersionUID = 1;

      public ProviderNotFoundException(final String msg) {
        super(msg);
      }

    }

  }

  static SipNameResolver newNameResolver(final URI targetUri) {
    return Registry.defaultRegistry().newNameResolver(targetUri);
  }

  static SipNameResolver newNameResolver(final HostPort target) {
    return Registry.defaultRegistry().newNameResolver(target);
  }

}
