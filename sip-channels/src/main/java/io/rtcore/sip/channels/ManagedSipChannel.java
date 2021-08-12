package io.rtcore.sip.channels;

import java.util.concurrent.Flow;

/**
 * a general transport interface which can be used
 */

public interface ManagedSipChannel extends SipTransport {

  /**
   * start receiving events from this transport.
   */

  Flow.Publisher<Event> start();

  /**
   * shut down cleanly, no new streams can be created but existing ones will continue until
   * terminated.
   */

  void shutdown(String reason);

  /**
   * perform a forced shutdown, all open streams are terminated.
   */

  void shutdownNow(String reason);

  /**
   *
   */

  interface Event {
    //
  }

  public final class ShutdownEvent implements Event {

    private final String reason;

    ShutdownEvent(final String reason) {
      this.reason = reason;
    }

    public String reason() {
      return this.reason;
    }

  }

  public enum Events implements Event {
    READY,
    // SHUTDOWN receives it's own.
    TERMINATED,
    IN_USE,
    IDLE
  }

}
