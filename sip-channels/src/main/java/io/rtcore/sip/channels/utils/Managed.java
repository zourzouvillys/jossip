package io.rtcore.sip.channels.utils;

import java.util.concurrent.Flow;

import io.rtcore.sip.channels.endpoint.SipEndpoint;

public interface Managed {

  /**
   * start the {@link SipEndpoint} upon the first subscriber, returning a publisher which provides
   * the state changes, completing the subscription once the state is terminal.
   *
   * if the service fails, the subscription will error. if the subscription is cancelled, the
   * service will perform an orderly shutdown.
   *
   */

  Flow.Publisher<State> start();

  /**
   * The lifecycle states of a managed service.
   */

  public enum State {
    NEW,
    STARTING,
    RUNNING,
    STOPPING,
    TERMINATED,
    FAILED,
  }

}
