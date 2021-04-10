package io.rtcore.sip.sigcore;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "")
public abstract class AbstractAddress {

  @Nullable
  @Value.Parameter
  public abstract String namespace();

  @Nullable
  @Value.Parameter
  public abstract String type();

  @Nullable
  @Value.Parameter
  public abstract String id();

}
