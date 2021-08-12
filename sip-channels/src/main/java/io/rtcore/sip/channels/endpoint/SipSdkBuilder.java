package io.rtcore.sip.channels.endpoint;

import java.util.function.Consumer;

public interface SipSdkBuilder<B extends SipSdkBuilder<B, T>, T> {

  /**
   * construct a new instance.
   */

  T build();

  /**
   * apply a mutator to the builder.
   */

  @SuppressWarnings("unchecked")
  default B applyMutation(final Consumer<B> mutator) {
    mutator.accept((B) this);
    return (B) this;
  }

}
