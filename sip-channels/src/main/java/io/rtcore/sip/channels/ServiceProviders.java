package io.rtcore.sip.channels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import com.google.common.annotations.VisibleForTesting;

final class ServiceProviders {

  private ServiceProviders() {
    // do not instantiate
  }

  /**
   * If this is not Android, returns the highest priority implementation of the class via
   * {@link ServiceLoader}. If this is Android, returns an instance of the highest priority class in
   * {@code hardcoded}.
   */
  public static <T> T load(
      final Class<T> klass,
      final Iterable<Class<?>> hardcoded,
      final ClassLoader cl,
      final PriorityAccessor<T> priorityAccessor) {
    final List<T> candidates = loadAll(klass, hardcoded, cl, priorityAccessor);
    if (candidates.isEmpty()) {
      return null;
    }
    return candidates.get(0);
  }

  /**
   * If this is not Android, returns all available implementations discovered via
   * {@link ServiceLoader}. If this is Android, returns all available implementations in
   * {@code hardcoded}. The list is sorted in descending priority order.
   */
  public static <T> List<T> loadAll(
      final Class<T> klass,
      final Iterable<Class<?>> hardcoded,
      final ClassLoader cl,
      final PriorityAccessor<T> priorityAccessor) {
    Iterable<T> candidates;
    if (isAndroid(cl)) {
      candidates = getCandidatesViaHardCoded(klass, hardcoded);
    }
    else {
      candidates = getCandidatesViaServiceLoader(klass, cl);
    }
    final List<T> list = new ArrayList<>();
    for (final T current : candidates) {
      if (!priorityAccessor.isAvailable(current)) {
        continue;
      }
      list.add(current);
    }

    // Sort descending based on priority. If priorities are equal, compare the class names to
    // get a reliable result.
    Collections.sort(list, Collections.reverseOrder((f1, f2) -> {
      final int pd = priorityAccessor.getPriority(f1) - priorityAccessor.getPriority(f2);
      if (pd != 0) {
        return pd;
      }
      return f1.getClass().getName().compareTo(f2.getClass().getName());
    }));
    return Collections.unmodifiableList(list);
  }

  /**
   * Returns true if the {@link ClassLoader} is for android.
   */
  static boolean isAndroid(final ClassLoader cl) {
    try {
      // Specify a class loader instead of null because we may be running under Robolectric
      Class.forName("android.app.Application", /* initialize= */ false, cl);
      return true;
    }
    catch (final Exception e) {
      // If Application isn't loaded, it might as well not be Android.
      return false;
    }
  }

  /**
   * Loads service providers for the {@code klass} service using {@link ServiceLoader}.
   */
  @VisibleForTesting
  public static <T> Iterable<T> getCandidatesViaServiceLoader(final Class<T> klass, final ClassLoader cl) {
    Iterable<T> i = ServiceLoader.load(klass, cl);
    // Attempt to load using the context class loader and ServiceLoader.
    // This allows frameworks like http://aries.apache.org/modules/spi-fly.html to plug in.
    if (!i.iterator().hasNext()) {
      i = ServiceLoader.load(klass);
    }
    return i;
  }

  /**
   * Load providers from a hard-coded list. This avoids using getResource(), which has performance
   * problems on Android (see https://github.com/grpc/grpc-java/issues/2037).
   */
  @VisibleForTesting
  static <T> Iterable<T> getCandidatesViaHardCoded(final Class<T> klass, final Iterable<Class<?>> hardcoded) {
    final List<T> list = new ArrayList<>();
    for (final Class<?> candidate : hardcoded) {
      list.add(create(klass, candidate));
    }
    return list;
  }

  @VisibleForTesting
  static <T> T create(final Class<T> klass, final Class<?> rawClass) {
    try {
      return rawClass.asSubclass(klass).getConstructor().newInstance();
    }
    catch (final Throwable t) {
      throw new ServiceConfigurationError(
        String.format("Provider %s could not be instantiated %s", rawClass.getName(), t),
        t);
    }
  }

  /**
   * An interface that allows us to get priority information about a provider.
   */
  public interface PriorityAccessor<T> {
    /**
     * Checks this provider is available for use, taking the current environment into consideration.
     * If {@code false}, no other methods are safe to be called.
     */
    boolean isAvailable(T provider);

    /**
     * A priority, from 0 to 10 that this provider should be used, taking the current environment
     * into consideration. 5 should be considered the default, and then tweaked based on environment
     * detection. A priority of 0 does not imply that the provider wouldn't work; just that it
     * should be last in line.
     */
    int getPriority(T provider);
  }
}
