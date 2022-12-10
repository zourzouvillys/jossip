package io.rtcore.sip.common;

import java.util.Optional;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(
    jdkOnly = true,
    allowedClasspathAnnotations = { Override.class },
    typeAbstract = "_*",
    typeImmutable = "*",
    visibility = ImplementationVisibility.PUBLIC,
    defaults = @Value.Immutable(builder = false, copy = false))
abstract class _SipDialogId {

  @Value.Parameter
  public abstract String callId();

  @Value.Parameter
  public abstract Optional<String> localTag();

  @Value.Parameter
  public abstract Optional<String> remoteTag();

}
