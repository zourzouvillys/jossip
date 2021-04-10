package io.rtcore.sip.sigcore;

import java.util.Optional;

public interface ToInvocation {

  /**
   * The address of the function that requested the invocation.
   * 
   * If this was not called by a function, it will be absent.
   */

  Optional<Address> caller();

  /**
   * the argument for the invocation.
   */
  
  Optional<TypedValue> argument();

}
