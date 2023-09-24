package io.rtcore.gateway.engine.http.server;

import java.util.concurrent.Flow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public interface ExternalSipServerHandler {

  interface RequestEvent {

    static RequestEvent fromString(final String string) {

      return new RequestEvent() {

        @Override
        public String toString() {
          return string;
        }

      };

    }

  }

  interface ResponseEvent {

    static ResponseEvent statusCode(final int statusCode) {
      return new SipResponseEvent(statusCode);
    }

    @JsonSerialize
    static class SipResponseEvent implements ResponseEvent {

      private final int statusCode;

      public SipResponseEvent(final int statusCode) {
        this.statusCode = statusCode;
      }

      @JsonProperty
      public int statusCode() {
        return this.statusCode;
      }

    }

  }

  /**
   * called when a new incoming SIP request is received over the external (http/gRPC) interface.
   *
   * the same interface is used for INVITE and non-INVITE requests. However, only INVITE requests
   * can have more than a single SIP response.
   *
   * @param request
   *          the request
   *
   * @return a stream of events which will be sent back to the caller.
   *
   */

  Flow.Publisher<ResponseEvent> handleRequest(RequestEvent request);

  /**
   * called to handle an incoming ACK.
   */

  Flow.Publisher<ResponseEvent> handleAck(RequestEvent request);

}
