package io.rtcore.sip.common;

import java.net.URI;
import java.util.Optional;

import org.immutables.value.Value;

import io.rtcore.sip.common.iana.SipMethodId;
import io.rtcore.sip.common.iana.SipStatusCodes;

public interface SipInitialLine {

  @Value.Immutable(builder = false)
  @Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class }, attributeBuilderDetection = true, deepImmutablesDetection = true)
  interface RequestLine extends SipInitialLine {

    @Value.Parameter
    SipMethodId method();

    @Value.Parameter
    URI uri();

  }

  @Value.Immutable(builder = false)
  @Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class }, attributeBuilderDetection = true, deepImmutablesDetection = true)
  interface ResponseLine extends SipInitialLine {

    @Value.Parameter
    int code();

    @Value.Parameter
    Optional<String> reason();

  }

  static ImmutableResponseLine of(SipStatusCodes status) {
    return ImmutableResponseLine.of(status.statusCode(), Optional.ofNullable(status.reasonPhrase()));
  }

}
