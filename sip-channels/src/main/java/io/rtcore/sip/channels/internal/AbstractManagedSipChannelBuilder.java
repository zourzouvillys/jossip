package io.rtcore.sip.channels.internal;

import java.time.Duration;

public abstract class AbstractManagedSipChannelBuilder<T extends ManagedSipChannelBuilder<T>> implements ManagedSipChannelBuilder<T> {

  @Override
  public T maxRetryAttempts(final int retries) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: ManagedSipChannelBuilder<T>.maxRetryAttempts invoked.");
  }

  @Override
  public T disableRetries() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: ManagedSipChannelBuilder<T>.disableRetries invoked.");
  }

  @Override
  public T maxInboundMessageSize(final int maxSize) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: ManagedSipChannelBuilder<T>.maxInboundMessageSize invoked.");
  }

  @Override
  public T keepAliveWithoutActivity(final boolean enabled) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: ManagedSipChannelBuilder<T>.keepAliveWithoutActivity invoked.");
  }

  @Override
  public T keepaliveTime(final Duration keepaliveTime) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: ManagedSipChannelBuilder<T>.keepaliveTime invoked.");
  }

  @Override
  public T keepaliveTimeout(final Duration keepaliveTimeout) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: ManagedSipChannelBuilder<T>.keepaliveTimeout invoked.");
  }

  @Override
  public T idleTimeout(final Duration idleTimeout) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: ManagedSipChannelBuilder<T>.idleTimeout invoked.");
  }

  @Override
  public T intercept(final SipChannelInterceptor... interceptors) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: ManagedSipChannelBuilder<T>.intercept invoked.");
  }

  @Override
  public T useTransportSecurity() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: ManagedSipChannelBuilder<T>.useTransportSecurity invoked.");
  }

  @Override
  public T overrideAuthority(final String authority) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: ManagedSipChannelBuilder<T>.overrideAuthority invoked.");
  }

  @Override
  public T userAgent(final String userAgent) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: ManagedSipChannelBuilder<T>.userAgent invoked.");
  }

}
