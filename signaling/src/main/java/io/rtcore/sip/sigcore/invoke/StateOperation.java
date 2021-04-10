package io.rtcore.sip.sigcore.invoke;

import java.time.Duration;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.immutables.value.Value.Enclosing;

import io.rtcore.sip.sigcore.Address;
import io.rtcore.sip.sigcore.ExpirationSpec.ExpireMode;

@Enclosing
public interface StateOperation {

  /**
   * all state specific operations extend this, which allows for CRDTs in the future.
   */

  @Value.Immutable
  interface MergeState<T> extends StateOperation {

    @Value.Parameter
    String stateName();

    @Value.Parameter
    @Nullable
    T stateValue();

    @Value.Parameter
    @Nullable
    ExpireMode expiry();

  }

  /**
   * perform an invocation, logically in the same execution context.
   */

  @Value.Immutable
  interface Invoke<T> extends StateOperation {

    /**
     * an identifier which can be used (but not required) for correlating on the caller side as well
     * as allowing supporting cancellation for invocations which are async and can take a
     * significant time, e.g. a DNS lookup, remote HTTP GET, or database query.
     */

    @Value.Parameter
    @Nullable
    String id();

    /**
     * the target to invoke.
     */

    @Value.Parameter
    Address target();

    /**
     * the argument (if any) to supply when invoking.
     */

    @Value.Parameter
    @Nullable
    T argument();

  }

  /**
   * schedule an invocation in the future.
   */

  @Value.Immutable
  interface ScheduleInvoke<T> extends StateOperation {

    /**
     * an identifier to allow the state to update or cancel this scheduled invocation in the future.
     * 
     * this id will not be provided when invoking, it is only on the scheduler side.
     * 
     * to provide data to the target being invoked, use the argument parameter instead.
     * 
     * a timer can be re-scheduled or re-routed by providing the same id.
     * 
     * if a scheduled invocation has already expired and been delivered the id will no longer exist
     * and thus would schedule a new invocation.
     * 
     */

    @Value.Parameter
    String id();

    /**
     * the amount of delay before invoking relative to the current invocation time.
     */

    @Value.Parameter
    Duration delay();

    /**
     * the target to invoke.
     */

    @Value.Parameter
    Address target();

    /**
     * the argument (if any) to supply when invoking.
     */

    @Value.Parameter
    @Nullable
    T argument();

  }

  /**
   * attempts to cancel a pre-defined invoke with the specified key.
   * 
   * there is no guarantee that a cancel will succeed, or even if the receiver supports them.
   * 
   * however - if the invoke has not yet been dispatched, or executed, it will not even be
   * delivered.
   * 
   * if the invoke references a scheduled invoke, then it will not be triggered as long as it has
   * not yet been delivered.
   * 
   */

  @Value.Immutable
  interface CancelInvoke extends StateOperation {

    /**
     * the ID to attempt to cancel.
     */

    @Value.Parameter
    String id();

  }

  /**
   * publishes an event into an external async journal.
   */

  @Value.Immutable
  interface PublishMessage extends StateOperation {

    @Value.Parameter
    String target();

  }

}
