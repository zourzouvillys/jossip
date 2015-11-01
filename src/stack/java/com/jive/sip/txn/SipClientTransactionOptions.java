package com.jive.sip.txn;

import java.time.Duration;
import java.util.Set;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class SipClientTransactionOptions
{

  /**
   * If this request should trigger next-hop lookups. If false, if there isn't already pinned entries, then the request will fail
   * immediatly.
   */

  private boolean allowLookups;

  /**
   * If this request should allow triggering of connection establishment. If false, if there isn't already an established connection (or
   * UDP), then this request will fail.
   */

  private boolean allowTriggerConnection;

  /**
   * Total amount of time willing to wait for lookups to provide us with concrete targets, from the moment we start trying to lookup a hop
   * for request until we have results.
   */

  private Duration lookupMax;

  /**
   * Amount of time we're willing to wait in total for establishing a connection. This includes connecting the socket, and the TLS handshake
   * (if over TLS).
   */

  private Duration connectMax;

  /**
   * Total time we're willing to wait from transaction layer receiving the request until we are send on the wire. If a request isn't sent
   * after this much time out of the stack, then it will be returned. Unless this is a real-time request (e.g, INVITE), it's recommended to
   * keep this either null or higher than lookupMax + (connectMax * numLookupResults).
   */

  private Duration initialTime;

  /**
   * Amount of time per connection to get a response. If null, uses the connection's settings.
   */

  private Duration timerA;

  /**
   * status codes which should be mapped to next-hop failures rather than target ones. If null, uses the connection's settings.
   */

  @Singular
  private Set<Integer> nextHopFailures;

}
