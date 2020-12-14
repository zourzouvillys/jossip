package io.rtcore.sip.proxy.http.netty;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.DefaultHttp2DataFrame;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.DefaultHttp2HeadersFrame;
import io.netty.handler.codec.http2.DefaultHttp2ResetFrame;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2MultiplexHandler;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * various http2 utils for streaming.
 */

public class Http2Util {

  private static final Logger log = LoggerFactory.getLogger(Http2Util.class);

  public static ApplicationProtocolNegotiationHandler getServerAPNHandler(StreamDispatcher appctx) {

    ApplicationProtocolNegotiationHandler serverAPNHandler =
      new ApplicationProtocolNegotiationHandler(
        ApplicationProtocolNames.HTTP_2) {

        @Override
        protected void configurePipeline(ChannelHandlerContext ctx, String protocol) throws Exception {
          ChannelPipeline pipeline = ctx.pipeline();
          if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {

            pipeline

              // limits to 64 kbit/sec inbound per connection.
              // .addLast(new ChannelTrafficShapingHandler(0, 8000, 1_000, 15_000))

              //
              .addLast(Http2FrameCodecBuilder.forServer()
                .autoAckPingFrame(true)
                // .frameLogger(new Http2FrameLogger(LogLevel.INFO))
                .autoAckSettingsFrame(true)
                .build())
              // .addLast(Http2FrameCodecBuilder.forServer().autoAckPingFrame(true)
              // .initialSettings(Http2Settings.defaultSettings().maxConcurrentStreams(128)).build())
              //
              .addLast(new Http2MultiplexHandler(new StreamContextHandler(appctx)));

            return;
          }
          throw new IllegalStateException("Protocol: " + protocol + " not supported");
        }
      };
    return serverAPNHandler;
  }

  public static Iterable<String> extractAuthTokens(Http2Headers req) {

    //
    Iterable<String> tokens =
      Iterables.transform(req.getAll("authorization"),
        e -> Optional.of(e.toString())
          .filter(content -> content.startsWith("Bearer "))
          .map(content -> content.substring(7))
          .orElse(""));

    return Iterables.filter(tokens, str -> !str.isEmpty());

  }

  public static List<String> makePath(CharSequence path) {
    return Splitter.on('/').omitEmptyStrings().trimResults().splitToList(path);
  }

  /**
   * 
   * @param req
   * @param res
   */

  static void addCORS(Http2Headers req, Http2Headers res) {

    String origin = req.get("origin", "").toString();

    if (Strings.isNullOrEmpty(origin)) {
      return;
    }

    res.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
    res.add(HttpHeaderNames.VARY, "origin"); // we fail with some origins.
    res.add(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, "7200");
    res.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*, authorization");
    res.add(HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS, "*");

  }

  /**
   * perform a streaming response to the client.
   * 
   * @param ctx
   * @param stream
   * @param req
   * @param response
   */

  static void streamResponse(ChannelHandlerContext ctx, Http2FrameStream stream, Http2Headers req, Single<StreamingResponse> response) {

    // if the consumer goes away, attempt to cancel.
    Disposable handle = response.subscribe(res -> sendResponseWithBody(ctx, stream, req, res), err -> sendResponseError(ctx, stream, req, err));

    // if the consumer goes away before we get a response, attempt to cancel.
    ctx.channel().closeFuture().addListener(f -> handle.dispose());

  }

  private static void sendResponseWithBody(ChannelHandlerContext ctx, Http2FrameStream stream, Http2Headers req, StreamingResponse res) {

    // the response headers.
    Http2Headers headers = new DefaultHttp2Headers();

    headers.add(res.headers());

    if (headers.status() == null) {
      headers.status(res.status().codeAsText());
    }

    addCORS(req, headers);

    if (!headers.contains("date")) {
      headers.add("date", DateFormatter.format(Date.from(Instant.now())));
    }

    if (!headers.contains("server")) {
      headers.add("server", "rtfs/streaming");
    }

    Flowable<StreamEvent> body = res.body();

    ctx.writeAndFlush(new DefaultHttp2HeadersFrame(headers, body == null).stream(stream));

    // now bind to the body.
    if (body == null) {
      return;
    }

    Disposable handle =
      body.subscribe(
        chunk -> {

          // send the buffer.
          try {
            write(ctx, stream, chunk);
            ctx.flush();
          }
          catch (Exception ex) {
            log.error("error generating buffer: {}", ex.getMessage(), ex);

          }

        },
        err -> {

          log.error("got error in stream: {}", err.getMessage(), err);

          // we have already sent headers, so can only send reset.
          ctx.writeAndFlush(new DefaultHttp2ResetFrame(Http2Error.INTERNAL_ERROR));

        },
        () -> {

          // end of frames.
          ctx.writeAndFlush(new DefaultHttp2DataFrame(true));

        });

    // if the consumer goes away before we get a response, attempt to cancel.
    ctx.channel().closeFuture().addListener(f -> {
      log.info("client terminated stream");
      handle.dispose();
    });

  }

  private static void write(ChannelHandlerContext ctx, Http2FrameStream stream, StreamEvent event) throws IOException {

    ByteBuf outbuf = ctx.alloc().buffer();

    outbuf.writeBytes(("id: " + event.eventId() + "\n").getBytes());
    outbuf.writeBytes(("event: " + event.eventType() + "\n").getBytes());
    outbuf.writeBytes(("data: ").getBytes());

    byte[] out = Http2Util.jsonMapper().writeValueAsBytes(event.data());

    outbuf.writeBytes(out);
    outbuf.writeBytes(("\n\n").getBytes());

    // send
    ctx.writeAndFlush(new DefaultHttp2DataFrame(outbuf).stream(stream))
      .addListener(e -> log.info("send complete: {}", e));

  }

  private static void sendResponseError(ChannelHandlerContext ctx, Http2FrameStream stream, Http2Headers req, Throwable err) {
    try {
      Throwables.throwIfInstanceOf(err, ProblemException.class);
      sendError(ctx, stream, req, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }
    catch (ProblemException ex) {
      sendError(ctx, stream, req, ex.problem());
    }
  }

  /**
   * 
   * @param ctx
   * @param stream
   * @param reqheaders
   * @param status
   */

  static void sendError(
      ChannelHandlerContext ctx,
      Http2FrameStream stream,
      Http2Headers req,
      HttpResponseStatus status) {

    ImmutableProblem problem =
      ImmutableProblem.builder()
        .title(status.reasonPhrase())
        .status(status.code())
        .build();

    sendError(ctx, stream, req, problem);

  }

  /**
   * 
   * @param ctx
   * @param stream
   * @param reqheaders
   * @param status
   */

  static void sendError(
      ChannelHandlerContext ctx,
      Http2FrameStream stream,
      Http2Headers req,
      HttpResponseStatus status,
      Consumer<ImmutableProblem.Builder> b) {

    ImmutableProblem.Builder problem = ImmutableProblem.builder().title(status.reasonPhrase()).status(status.code());
    b.accept(problem);
    sendError(ctx, stream, req, problem.build());

  }

  static void sendError(
      ChannelHandlerContext ctx,
      Http2FrameStream stream,
      Http2Headers req,
      HttpResponseStatus status,
      String detail) {

    ImmutableProblem problem =
      ImmutableProblem.builder()
        .title(status.reasonPhrase())
        .status(status.code())
        .detail(detail)
        .build();

    sendError(ctx, stream, req, problem);

  }

  /**
   * 
   * @param ctx
   * @param stream
   * @param req
   * @param problem
   */

  static void sendError(
      ChannelHandlerContext ctx,
      Http2FrameStream stream,
      Http2Headers req,
      Problem problem) {
    Http2Headers headers = new DefaultHttp2Headers().status(Integer.toString(problem.status().orElse(500)));
    headers.add(HttpHeaderNames.CONTENT_TYPE, "application/problem+json");
    headers.add(HttpHeaderNames.CACHE_CONTROL, "no-cache");
    addCORS(req, headers);
    ByteBuf response;
    try {
      response = ctx.alloc().buffer().writeBytes(jsonMapper.writeValueAsBytes(problem));
    }
    catch (JsonProcessingException e) {
      log.error("failed to generate error message: {}", e.getMessage(), e);
      throw new RuntimeException(e);
    }
    ctx.write(new DefaultHttp2HeadersFrame(headers).stream(stream));
    ctx.writeAndFlush(new DefaultHttp2DataFrame(response, true).stream(stream));
  }

  /**
   * 
   */

  private static final JsonMapper jsonMapper =
    JsonMapper.builder()
      .addModule(new Jdk8Module())
      .addModule(new GuavaModule())
      .addModule(new JavaTimeModule())
      .build();

  static JsonMapper jsonMapper() {
    return jsonMapper;
  }

}
