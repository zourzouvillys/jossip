package io.rtcore.sip.channels.internal;

import java.time.Duration;

public interface ManagedSipChannelBuilder<T extends ManagedSipChannelBuilder<T>> {

  /**
   * Sets the maximum number of retry attempts that may be configured by the service config. If the
   * service config specifies a larger value it will be reduced to this value. Setting this number
   * to zero is not effectively the same as disableRetry() because the former does not disable
   * transparent retry.
   */

  T maxRetryAttempts(int retries);

  /**
   * disables all retries.
   */

  T disableRetries();

  /**
   * max number of bytes allowed in total for an incoming message.
   */

  T maxInboundMessageSize(int maxSize);

  /**
   * if keepalives should be performed when there is no activity on the channel.
   */

  T keepAliveWithoutActivity(boolean enabled);

  /**
   * Sets the time without read activity before sending a keepalive ping.
   */

  T keepaliveTime(Duration keepaliveTime);

  /**
   * Sets the time waiting for read activity after sending a keepalive ping. after this amount of
   * time, the connection is considered dead.
   */

  T keepaliveTimeout(Duration keepaliveTimeout);

  /**
   * how long after any call activity before the channel goes idle.
   */

  T idleTimeout(Duration idleTimeout);

  /**
   * intercept messages over this transport.
   */

  T intercept(SipChannelInterceptor... interceptors);

  /**
   * require transport security.
   */

  T useTransportSecurity();

  /**
   * the authority to expect.
   */

  T overrideAuthority(String authority);

  /**
   * if any messages need to be generated directly, then use this for the user-agent/server.
   */

  T userAgent(String userAgent);

  /**
   * build this channel
   */

  ManagedSipChannel build();

}
