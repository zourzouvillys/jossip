package io.rtcore.sip.sigcore.invoke;

import io.rtcore.sip.sigcore.Address;

public interface StateGateway {

  /**
   * triggers a signal to the specified address.
   * 
   * @param target
   *          The address to invoke.
   *          
   * @param signal
   *          The signal to raise to the specified target.
   * 
   */

  void signal(Address target, StateSignal signal);

}
