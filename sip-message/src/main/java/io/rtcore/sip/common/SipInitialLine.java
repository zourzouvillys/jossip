package io.rtcore.sip.common;

import java.net.URI;
import java.util.Optional;

import org.immutables.value.Value;

import io.rtcore.sip.common.iana.SipMethodId;
import io.rtcore.sip.common.iana.SipStatusCodes;

public sealed interface SipInitialLine permits SipInitialLine.RequestLine, SipInitialLine.ResponseLine {

  @Value.Immutable(builder = false)
  @Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class }, attributeBuilderDetection = true, deepImmutablesDetection = true)
  sealed interface RequestLine extends SipInitialLine permits ImmutableRequestLine {

    @Value.Parameter
    SipMethodId method();

    @Value.Parameter
    URI uri();

  }

  @Value.Immutable(builder = false)
  @Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class }, attributeBuilderDetection = true, deepImmutablesDetection = true)
  sealed interface ResponseLine extends SipInitialLine permits ImmutableResponseLine {

    @Value.Parameter
    int code();

    @Value.Parameter
    Optional<String> reason();

  }

  static ImmutableRequestLine of(SipMethodId method, URI uri) {
    return ImmutableRequestLine.of(method, uri);
  }

  static ImmutableResponseLine of(SipStatusCodes status) {
    return ImmutableResponseLine.of(status.statusCode(), Optional.ofNullable(status.reasonPhrase()));
  }

}
