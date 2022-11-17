package io.zrz.rtcore.useragent.api;

public interface UserAgent {

  // start a new invite transaction. specify target for events.
  //
  // - next hop/DNS resolution (unless connection is specified)
  // - network connection (including TLS handshake)
  // - send request
  //

  // cancel an ongoing transaction
  // - must specify cancellation token

  // send stateless ACK
  // - same as INVITE except no result

  // start NICT

  // -----

  // respond to NIST
  // respond to IST

}
