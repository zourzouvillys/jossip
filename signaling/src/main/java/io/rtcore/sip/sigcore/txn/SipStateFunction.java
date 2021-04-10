package io.rtcore.sip.sigcore.txn;

import java.util.Optional;

import io.rtcore.sip.sigcore.Address;
import io.rtcore.sip.sigcore.TypedValue;

public interface SipStateFunction {

  interface Context {

    /**
     * the caller.
     */

    Optional<Address> caller();

    /**
     * the value from this invocation.
     */

    Optional<TypedValue> argument();

    /**
     * the local address of this function invocation.
     */

    Address target();

    /**
     * access to the function state.
     */

    TypedValue state(String key);

  }

}
