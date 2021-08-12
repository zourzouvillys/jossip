package io.rtcore.sip.channels.netty.internal;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.base.Verify;

public final class RefCounted<T> {

  private final Supplier<T> alloc;
  private final Consumer<T> release;

  private int count = 0;
  private T instance;

  private RefCounted(final Supplier<T> alloc, final Consumer<T> release) {
    this.alloc = alloc;
    this.release = release;
  }

  public T get() {
    if (this.count == 0) {
      this.instance = this.alloc.get();
    }
    this.count++;
    return this.instance;
  }

  public boolean isInstance(final T instance) {
    return this.instance == instance;
  }

  public boolean release(final T instance) {

    Verify.verify(instance == this.instance);

    //
    --this.count;

    if (this.count > 0) {
      return false;
    }

    final T i = this.instance;
    this.instance = null;
    this.release.accept(i);
    return true;

  }

  public static <T> RefCounted<T> create(final Supplier<T> alloc, final Consumer<T> release) {
    return new RefCounted<>(alloc, release);
  }

}
