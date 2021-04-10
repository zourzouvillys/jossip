package io.rtcore.sip.sigcore.invoke.memory;

import java.util.HashMap;
import java.util.Map;

public class StateFunctionRegistry implements StateFunctionProvider {

  /**
   * 
   */
  
  private final Map<String, StateFunction> functions = new HashMap<>();

  /**
   * 
   */

  @Override
  public StateFunction provide(String namespace, String type) {
    return this.functions.get(String.format("%s:%s", namespace, type));
  }
  
  /**
   * 
   */

  public void register(String namespace, String type, StateFunction function) {
    this.functions.put(String.format("%s:%s", namespace, type), function);
  }

}
