package io.rtcore.sip.channels.auth;

import java.util.function.Function;

import org.immutables.value.Value;

import com.google.common.hash.HashFunction;

import io.rtcore.sip.common.iana.SipMethodId;

@Value.Immutable
@Value.Style(jdkOnly = true, stagedBuilder = true, allowedClasspathAnnotations = { Override.class })
public interface DigestContext {

  /**
   * the digest authentication realm
   */

  String realm();

  /**
   * the SIP method..
   */

  SipMethodId method();

  /**
   * the request URI.
   */

  String digestURI();

  /**
   * the entity hash provider.
   */

  Function<HashFunction, String> entityHash();

}
