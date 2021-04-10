package io.rtcore.sip.sigcore.invoke.memory;

import io.rtcore.sip.sigcore.Address;
import io.rtcore.sip.sigcore.invoke.StateGateway;
import io.rtcore.sip.sigcore.invoke.StateSignal;

/**
 * 
 * @author theo
 *
 */

public class StateProcessor implements StateGateway {

  /**
   * the underlying data store access.
   */

  private final StateStore store;

  /**
   * 
   */

  private final StateFunctionProvider functions;

  /**
   * 
   */

  public StateProcessor(StateStore store, StateFunctionProvider functions) {
    this.store = store;
    this.functions = functions;
  }

  /**
   * 
   */

  @Override
  public void signal(Address target, StateSignal signal) {

    //
    StateFunction func = this.functions.provide(target.namespace(), target.type());

    //
    System.err.println(func);

  }

  /**
   * 
   */

}
