package io.rtcore.sip.sigcore.invoke;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*")
public abstract class AbstractStateSignal {

  /**
   * the name of this signal.
   */

  @Value.Parameter
  public abstract String name();

}
