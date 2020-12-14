package io.rtcore.sip.proxy.http.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;

/**
 * created per instance for hte network side context.
 */

class StreamContext implements StreamRequest, StreamResponse {

  private static final Logger log = LoggerFactory.getLogger(StreamContext.class);

  StreamContext(ChannelHandlerContext ctx, StreamDispatcher dispatcher) {
  }

  /**
   * called to read an incoming message, returning true if it was handled. if false is returned the
   * message will be passed on in the pipeline.
   */

  boolean read(Object msg) {
    return true;
  }

  boolean readComplete() {
    return true;
  }

  boolean userEventTriggered(Object evt) {
    return true;
  }

  /**
   * the stream was closed.
   */

  void closed() {
    log.warn("client closed connection with buffered content");
  }

  void exceptionCaught(Throwable cause) {
  }

}
