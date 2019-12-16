/**
 *
 */
package com.jive.sip.processor.rfc3261.serializing;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.primitives.Primitives;

/**
 * provides serialization of the SIP model to actual SIP messages.
 */

public class RfcSerializerManager {

  private static final RfcSerializerManager INSTANCE = new RfcSerializerManagerBuilder().build();

  private final Map<Class<?>, RfcSerializer<?>> serializers = Maps.newHashMap();

  private final LoadingCache<Class<?>, RfcSerializer<?>> classSerializerCache =
    CacheBuilder
      .newBuilder()
      .build(CacheLoader.from(klass -> (RfcSerializer<?>) serializerFor((Class<?>) klass)));

  /**
   * register a serializer for a specific field type.
   * 
   * @param <T>
   * @param klass
   * @param serializer
   */

  public <T> void register(final Class<? extends T> klass, final RfcSerializer<T> serializer) {
    this.serializers.put(klass, serializer);
  }

  /**
   * 
   */

  public String writeValueAsString(Object obj) {
    return serialize(obj);
  }

  /**
   * use {@link #serialize(Writer, Object)} instead.
   *
   * @param obj
   * @return
   */

  @Deprecated
  @SuppressWarnings("unchecked")
  public <T> String serialize(final T obj) {

    if (obj instanceof Collection) {
      return this.serializeCollection(Collection.class.cast(obj), "=");
    }
    if (Primitives.isWrapperType(obj.getClass()) || (obj instanceof String)) {
      return "" + obj;
    }

    final RfcSerializer<?> serialixer = this.classSerializerCache.getUnchecked(obj.getClass());

    if (serialixer == null) {
      throw new RuntimeException("No serializer registered for " + obj.getClass().getName());
    }

    return ((RfcSerializer<T>) serialixer).serialize(obj);

  }

  @SuppressWarnings("unchecked")
  public <T> void serialize(final Writer sb, final T obj) throws IOException {

    Preconditions.checkNotNull(obj);

    if (Collection.class.isAssignableFrom(obj.getClass())) {
      // TODO: it seems this doesn't belong here, as different collections use different seperators
      // ...
      sb.append(this.serializeCollection(Collection.class.cast(obj), "="));
    }
    else if (Primitives.isWrapperType(obj.getClass()) || (obj instanceof String)) {
      sb.append(obj.toString());
    }
    else {
      final RfcSerializer<T> serializer = (RfcSerializer<T>) classSerializerCache.getUnchecked(obj.getClass());
      if (serializer == null) {
        throw new RuntimeException("No serializer registered for " + obj.getClass().getName());
      }
      serializer.serialize(sb, obj);
    }

  }

  /**
   * Use {@link #serializeCollection(Writer, Collection, String)} instead.
   *
   * @param collection
   * @param separator
   * @return
   */

  @Deprecated
  public <T> String serializeCollection(final Collection<T> collection, final String separator) {

    return toString(w -> serializeCollection(w, collection, separator));

    // serializeCollection(writer, collection, separator);

    // final Joiner joiner = Joiner.on(separator).skipNulls();
    // final List<String> lines = Lists.newArrayList();
    // for (final T t : collection) {
    // lines.add(this.serialize(t));
    // }
    // return joiner.join(lines);
  }

  interface UncheckedConsumer<T> {
    void apply(T value) throws Exception;
  }

  private String toString(UncheckedConsumer<Writer> generator) {
    StringWriter w = new StringWriter();
    try {
      generator.apply(w);
    }
    catch (RuntimeException ex) {
      throw ex;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    return w.toString();
  }

  public <T> void serializeCollection(final Writer sb, final Collection<T> collection, final String separator) throws IOException {

    if (collection == null) {
      return;
    }

    int i = 0;

    for (final T item : collection) {
      if (i++ > 0) {
        sb.append(separator);
      }
      this.serialize(sb, item);
    }

  }

  @SuppressWarnings("unchecked")
  private <T> RfcSerializer<T> serializerFor(Class<T> klass) {
    final RfcSerializer<T> serializer = (RfcSerializer<T>) this.serializers.get(klass);
    if (serializer != null) {
      return serializer;
    }
    for (Class<?> iface : klass.getInterfaces()) {
      RfcSerializer<?> res = serializerFor(iface);
      if (res != null) {
        return (RfcSerializer<T>) res;
      }
    }
    return (RfcSerializer<T>) serializerFor(klass.getSuperclass());
  }

  public static final RfcSerializerManager defaultSerializer() {
    return INSTANCE;
  }

}
