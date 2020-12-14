package io.rtcore.sip.proxy.http.netty;

public interface StreamDispatcher {

  /**
   * request for the application to handle a stream.
   */
  
  void handle(StreamRequest req, StreamResponse res);

}
