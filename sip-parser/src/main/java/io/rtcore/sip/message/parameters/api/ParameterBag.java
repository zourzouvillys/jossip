package io.rtcore.sip.message.parameters.api;

public final class ParameterBag {

  private static final ParameterBag EMPTY = new ParameterBag();

  private ParameterBag() {
  }

  @Override
  public String toString() {
    return "{}";
  }

  public static ParameterBag of() {
    return EMPTY;
  }

}
