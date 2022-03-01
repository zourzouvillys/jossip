package io.rtcore.sip.channels.netty.tcp;

public interface SipConnectionProvider {

  /**
   * request a connection for the specified route.
   * 
   * the returned connection can be used for SIP exchanges.
   * 
   * closing the connection will return it to the pool if it comes from one.
   * 
   * the returned connection may not yet be open, or may have rate limiting restricting how quickly
   * it can be sent. the connection may also fail at any point, or already be in a failed state by
   * the time it is returned to the caller. only by sending an exchange or a message can it be
   * validated.
   * 
   */

  SipConnection requestConnection(SipRoute route);

}
