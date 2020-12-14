package io.rtcore.sip.proxy.http.netty;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.EmptyHttp2Headers;
import io.netty.handler.codec.http2.Http2Headers;
import io.reactivex.rxjava3.core.Flowable;

// used for all non error responses.
@Value.Immutable
public interface StreamingResponse {

  /**
   * the HTTP response status code to send.
   */

  @Value.Parameter
  HttpResponseStatus status();

  /**
   * the H2 headers.
   */

  @Value.Parameter
  Http2Headers headers();

  /**
   * the body to stream back to the client.
   */

  @Value.Parameter
  @Nullable
  Flowable<StreamEvent> body();

  /**
   * some helpers
   * 
   * @param code
   * @return
   */

  public static ImmutableStreamingResponse of(int code) {
    return ImmutableStreamingResponse.of(HttpResponseStatus.valueOf(code), EmptyHttp2Headers.INSTANCE, null);
  }

  public static ImmutableStreamingResponse of(int code, StreamEvent body) {
    return ImmutableStreamingResponse.of(HttpResponseStatus.valueOf(code), EmptyHttp2Headers.INSTANCE, Flowable.just(body));
  }

  public static ImmutableStreamingResponse of(StreamEvent body) {
    return ImmutableStreamingResponse.of(HttpResponseStatus.OK, EmptyHttp2Headers.INSTANCE, Flowable.just(body));
  }

  public static ImmutableStreamingResponse of(HttpResponseStatus status, Flowable<StreamEvent> body) {
    return ImmutableStreamingResponse.of(status, EmptyHttp2Headers.INSTANCE, body);
  }

  public static ImmutableStreamingResponse of(HttpResponseStatus status) {
    return ImmutableStreamingResponse.of(status, EmptyHttp2Headers.INSTANCE, null);
  }

  public static ImmutableStreamingResponse of(int code, Flowable<StreamEvent> body) {
    return ImmutableStreamingResponse.of(HttpResponseStatus.valueOf(code), EmptyHttp2Headers.INSTANCE, body);
  }

}
