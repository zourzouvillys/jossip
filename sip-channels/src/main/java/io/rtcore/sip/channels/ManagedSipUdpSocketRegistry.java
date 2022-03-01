package io.rtcore.sip.channels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class ManagedSipUdpSocketRegistry {

  private static final Logger logger = Logger.getLogger(ManagedSipUdpSocketRegistry.class.getName());
  private static ManagedSipUdpSocketRegistry instance;

  private final LinkedHashSet<ManagedSipUdpSocketProvider> allProviders = new LinkedHashSet<>();

  /** Immutable, sorted version of {@code allProviders}. Is replaced instead of mutating. */
  private List<ManagedSipUdpSocketProvider> effectiveProviders = Collections.emptyList();

  /**
   * Register a provider.
   *
   * <p>
   * If the provider's {@link ManagedSipUdpSocketProvider#isAvailable isAvailable()} returns
   * {@code false}, this method will throw {@link IllegalArgumentException}.
   *
   * <p>
   * Providers will be used in priority order. In case of ties, providers are used in registration
   * order.
   */
  public synchronized void register(final ManagedSipUdpSocketProvider provider) {
    this.addProvider(provider);
    this.refreshProviders();
  }

  private synchronized void addProvider(final ManagedSipUdpSocketProvider provider) {
    Preconditions.checkArgument(provider.isAvailable(), "isAvailable() returned false");
    this.allProviders.add(provider);
  }

  /**
   * Deregisters a provider. No-op if the provider is not in the registry.
   *
   * @param provider
   *          the provider that was added to the register via {@link #register}.
   */
  public synchronized void deregister(final ManagedSipUdpSocketProvider provider) {
    this.allProviders.remove(provider);
    this.refreshProviders();
  }

  private synchronized void refreshProviders() {
    final List<ManagedSipUdpSocketProvider> providers = new ArrayList<>(this.allProviders);
    // Sort descending based on priority.
    // sort() must be stable, as we prefer first-registered providers
    Collections.sort(providers, Collections.reverseOrder((o1, o2) -> o1.priority() - o2.priority()));
    this.effectiveProviders = Collections.unmodifiableList(providers);
  }

  /**
   * Returns the default registry that loads providers via the Java service loader mechanism.
   */
  public static synchronized ManagedSipUdpSocketRegistry getDefaultRegistry() {
    if (instance == null) {
      final List<ManagedSipUdpSocketProvider> providerList =
          ServiceProviders.loadAll(
            ManagedSipUdpSocketProvider.class,
            List.of(),
            ManagedSipUdpSocketProvider.class.getClassLoader(),
            new ManagedChannelPriorityAccessor());
      instance = new ManagedSipUdpSocketRegistry();
      for (final ManagedSipUdpSocketProvider provider : providerList) {
        logger.fine("Service loader found " + provider);
        if (provider.isAvailable()) {
          instance.addProvider(provider);
        }
      }
      instance.refreshProviders();
    }
    return instance;
  }

  /**
   * Returns effective providers, in priority order.
   */
  @VisibleForTesting
  synchronized List<ManagedSipUdpSocketProvider> providers() {
    return this.effectiveProviders;
  }

  // For emulating ManagedChannelProvider.provider()
  ManagedSipUdpSocketProvider provider() {
    final List<ManagedSipUdpSocketProvider> providers = this.providers();
    return providers.isEmpty() ? null
                               : providers.get(0);
  }

  ManagedSipUdpSocketBuilder<?> newBuilder() {

    final List<ManagedSipUdpSocketProvider> providers = this.providers();

    if (providers.isEmpty()) {
      throw new ProviderNotFoundException("No functional udp socket service provider found.");
    }

    final StringBuilder error = new StringBuilder();

    for (final ManagedSipUdpSocketProvider provider : this.providers()) {

      final ManagedSipUdpSocketBuilder<?> builder = provider.builder();

      if (builder != null) {
        return builder;
      }

      error.append("; ");
      error.append(provider.getClass().getName());

    }

    throw new ProviderNotFoundException(error.substring(2));
  }

  private static final class ManagedChannelPriorityAccessor implements ServiceProviders.PriorityAccessor<ManagedSipUdpSocketProvider> {

    @Override
    public boolean isAvailable(final ManagedSipUdpSocketProvider provider) {
      return provider.isAvailable();
    }

    @Override
    public int getPriority(final ManagedSipUdpSocketProvider provider) {
      return provider.priority();
    }

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
