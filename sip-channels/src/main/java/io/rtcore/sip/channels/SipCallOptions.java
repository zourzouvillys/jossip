package io.rtcore.sip.channels;

import java.util.Optional;

import org.immutables.value.Value;

import io.rtcore.sip.common.Host;

/**
 *
 */

@Value.Immutable(singleton = true)
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public interface SipCallOptions {

  /**
   * the call credentials. note that there can be a number of credentials for multiple
   * realms/domains, and can be both WWW-Auth and Proxy-Auth.
   */

  Optional<SipCallCredentials> credentials();

  /**
   * the target authority for this call.
   */

  Optional<Host> authority();

  /**
   *
   */

  static ImmutableSipCallOptions of() {
    return ImmutableSipCallOptions.of();
  }

}
