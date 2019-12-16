/**
 *
 */
package com.jive.sip.processor.rfc3261.serializing;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Primitives;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */
public class RfcSerializerManager {
  private final Map<Class<?>, RfcSerializer<?>> serializers = Maps.newHashMap();

  public <T> void register(final Class<? extends T> klass, final RfcSerializer<T> serializer) {
    this.serializers.put(klass, serializer);
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

    final RfcSerializer<?> serialixer = this.getSerializer(obj.getClass());

    if (serialixer == null) {
      throw new RuntimeException("No serializer registered for " + obj.getClass().getName());
    }

    return ((RfcSerializer<T>) serialixer).serialize(obj);

  }

  private RfcSerializer<?> getSerializer(final Class<?> klass) {

    if (this.serializers.containsKey(klass)) {
      return this.serializers.get(klass);
    }

    for (final Class<?> cls : getAllInterfaces(klass)) {
      if (this.serializers.containsKey(cls)) {
        return this.serializers.get(cls);
      }
    }

    for (final Class<?> cls : getAllSuperClasses(klass)) {
      if (this.serializers.containsKey(cls)) {
        return this.serializers.get(cls);
      }
    }

    return null;

  }

  public static List<Class<?>> getAllSuperClasses(Class<?> cls) {

    final List<Class<?>> klasses = new ArrayList<>();

    cls = cls.getSuperclass();

    while (cls != null) {
      klasses.add(cls);
      cls = cls.getSuperclass();
    }

    return klasses;

  }

  public static List<Class<?>> getAllInterfaces(Class<?> cls) {
    if (cls == null) {
      return null;
    }
    final List list = new ArrayList<>();
    while (cls != null) {
      final Class[] interfaces = cls.getInterfaces();
      for (final Class interface1 : interfaces) {
        if (list.contains(interface1) == false) {
          list.add(interface1);
        }
        final List superInterfaces = getAllInterfaces(interface1);
        for (final Iterator it = superInterfaces.iterator(); it.hasNext();) {
          final Class intface = (Class) it.next();
          if (list.contains(intface) == false) {
            list.add(intface);
          }
        }
      }
      cls = cls.getSuperclass();
    }
    return list;
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
    else if (!this.serializers.containsKey(obj.getClass())) {
      throw new RuntimeException("No serializer registered for " + obj.getClass().getName());
    }
    else {
      final RfcSerializer<T> serializer = (RfcSerializer<T>) this.serializers.get(obj.getClass());
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
    final Joiner joiner = Joiner.on(separator).skipNulls();
    final List<String> lines = Lists.newArrayList();
    for (final T t : collection) {
      lines.add(this.serialize(t));
    }
    return joiner.join(lines);
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

}
