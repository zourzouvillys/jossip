package io.rtcore.sip.channels.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class SipAttributes {

  /**
   * A unique key for a set of attributes. note that each key is unique by ref not value - two keys
   * with the same debug string are not the same key.
   * 
   * @param <T>
   */

  public static final class Key<T> {

    private final String debugString;

    private Key(final String debugString) {
      this.debugString = debugString;
    }

    public static <T> Key<T> create(final String debugString) {
      return new Key<>(debugString);
    }

    @Override
    public String toString() {
      return this.debugString;
    }

  }

  private static final SipAttributes EMPTY = new SipAttributes(Map.of());

  private final Map<Key<?>, Object> data;

  private SipAttributes(final Map<Key<?>, Object> data) {
    this.data = Map.copyOf(data);
  }

  /**
   *
   */

  public static final class Builder {

    private final Map<Key<?>, Object> data;

    private Builder(final SipAttributes initialState) {
      this.data = new HashMap<>(initialState.data);
    }

    public <T> Builder setAll(final SipAttributes other) {
      this.data.putAll(other.data);
      return this;
    }

    public <T> Builder set(final Key<T> key, final T value) {
      this.data.put(key, value);
      return this;
    }

    public <T> Builder set(final Key<T> key, final Optional<T> value) {
      value.ifPresentOrElse(existing -> set(key, existing), () -> discard(key));
      return this;
    }

    public <T> Builder discard(final Key<T> key) {
      this.data.remove(key);
      return this;
    }

    public SipAttributes build() {
      if (this.data.isEmpty()) {
        return SipAttributes.of();
      }
      return new SipAttributes(this.data);
    }

  }

  public static Builder newBuilder() {
    return new Builder(of());
  }

  public <T> SipAttributes withAttribute(final Key<T> key, final T value) {
    return this.toBuilder().set(key, value).build();
  }

  public <T> SipAttributes withoutAttribute(final Key<T> key) {
    return this.toBuilder().discard(key).build();
  }

  public static SipAttributes of() {
    return EMPTY;
  }

  /**
   *
   */

  // unchecked because only ever accessible via the same key instance so can't break without
  // casting...
  @SuppressWarnings("unchecked")
  public <T> Optional<T> get(final Key<T> key) {
    return Optional.ofNullable((T) this.data.get(key));
  }

  @SuppressWarnings("unchecked")
  public <T> T getOrDefault(final Key<T> key, T defaultValue) {
    if (this.data.containsKey(key))
      return (T) this.data.get(key);
    return defaultValue;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public String toString() {
    return this.data.toString();
  }

  /**
   * Returns true if the given object is also a {@link SipAttributes} with an equal attribute
   * values.
   *
   * <p>
   * Note that if a stored values are mutable, it is possible for two objects to be considered equal
   * at one point in time and not equal at another (due to concurrent mutation of attribute values).
   *
   * <p>
   * This method is not implemented efficiently and is meant for testing.
   *
   * @param o
   *          an object.
   * @return true if the given object is a {@link SipAttributes} equal attributes.
   */

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if ((o == null) || (this.getClass() != o.getClass())) {
      return false;
    }
    final SipAttributes that = (SipAttributes) o;
    if (this.data.size() != that.data.size()) {
      return false;
    }
    for (final Map.Entry<Key<?>, Object> e : this.data.entrySet()) {
      if (!that.data.containsKey(e.getKey()) || !Objects.equals(e.getValue(), that.data.get(e.getKey()))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns a hash code for the attributes.
   *
   * <p>
   * Note that if a stored values are mutable, it is possible for two objects to be considered equal
   * at one point in time and not equal at another (due to concurrent mutation of attribute values).
   *
   * @return a hash code for the attributes map.
   */

  @Override
  public int hashCode() {
    return Objects.hash(this.data.entrySet().toArray());
  }

}
