package io.rtcore.sip.channels.api;

import org.immutables.value.Value;

/**
 *
 */

@Value.Immutable(singleton = true)
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public interface SipCallOptions {

  /**
   *
   */

  static ImmutableSipCallOptions of() {
    return ImmutableSipCallOptions.of();
  }

}
