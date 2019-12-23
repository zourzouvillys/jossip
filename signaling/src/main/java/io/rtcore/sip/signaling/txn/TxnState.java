package io.rtcore.sip.signaling.txn;

import java.time.Instant;
import java.util.List;

import io.rtcore.sip.signaling.txn.actions.EmittableEvent;
import io.rtcore.sip.signaling.txn.actions.ListenableEvent;

public interface TxnState {

  /**
   * the next timeout to dispatch based on the current transaction state.
   */

  Instant nextTimout();

  /**
   * the incoming event queue.
   */

  List<ListenableEvent> in();

  /**
   * the outgoing event queue. generally this requires the caller to process the event, then remove
   * from the outgoing queue.
   */

  List<EmittableEvent> out();

}
