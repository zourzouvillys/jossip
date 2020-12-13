package io.rtcore.sip.proxy.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.eventbus.EventBus;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.DefaultHttp2DataFrame;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.DefaultHttp2HeadersFrame;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2Headers;
import io.rtcore.sip.proxy.actions.OpenStream;

final class IncomingRequestStream {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(IncomingRequestStream.class);
  //
  private final EventBus bus;
  private final ChannelHandlerContext ctx;
  private final Http2FrameStream stream;
  private Http2Headers headers;
  private ByteArrayOutputStream baos = new ByteArrayOutputStream();

  private static final JsonMapper mapper = new JsonMapper();
  static {
    mapper.registerModule(new Jdk8Module());
  }

  IncomingRequestStream(EventBus bus, ChannelHandlerContext ctx, Http2FrameStream stream) {
    this.bus = bus;
    this.ctx = ctx;
    this.stream = stream;
  }

  public void headers(Http2Headers headers, boolean endStream) {

    this.headers = headers;

    log.info(
      "method: {}, authority: {}, path: {}, content-type: {}",
      headers.method(),
      headers.authority(),
      headers.path(),
      headers.get(HttpHeaderNames.CONTENT_TYPE, "<none>"));

    if (endStream) {
      this.process();
    }

  }

  public void data(ByteBuf content, boolean endStream) {

    try {
      content.readBytes(this.baos, content.readableBytes());
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
    finally {
      content.release();
    }

    if (endStream) {
      this.process();
    }

  }

  public void windowUpdate(int windowSizeIncrement) {
    log.info("window update: {}", windowSizeIncrement);
  }

  public void reset() {
    log.info("got reset");
  }

  private void process() {

    log.info("processing stream");

    switch (headers.method().toString()) {
      case "POST":
        readInput(OpenStream.class, this::process);
        return;
      case "GET":
      case "PUT":
      case "DELETE":
      case "PATCH":
      case "OPTIONS":
        break;
    }

    // start by

    DefaultHttp2Headers res = new DefaultHttp2Headers(true);

    res
      .status(HttpResponseStatus.METHOD_NOT_ALLOWED.codeAsText())
      .add(HttpHeaderNames.CONTENT_TYPE, "application/problem+json");

    ctx.write(new DefaultHttp2HeadersFrame(res).stream(stream));
    ByteBuf content = ctx.alloc().buffer();

    content.writeBytes(this.baos.toByteArray());
    ByteBufUtil.writeAscii(content, "{}\n");

    ctx.write(new DefaultHttp2DataFrame(content, true).stream(stream));

  }

  private <T> void readInput(Class<T> valueType, Consumer<T> handler) {
    final T value;
    try {
      value = readValue(valueType);
    }
    catch (Exception ex) {
      sendError(HttpResponseStatus.BAD_REQUEST, ex.getMessage());
      return;
    }
    handler.accept(value);
  }

  private void sendError(HttpResponseStatus status, String message) {
    DefaultHttp2Headers res = new DefaultHttp2Headers(true);
    res.status(status.codeAsText()).add(HttpHeaderNames.CONTENT_TYPE, "application/problem+json");
    ctx.write(new DefaultHttp2HeadersFrame(res).stream(stream));
    ByteBuf content = ctx.alloc().buffer();
    ObjectNode body = JsonNodeFactory.instance.objectNode();
    body.put("type", "https://rtfsip.io/problems/json-error");
    body.put("title", "JSON deserialization error");
    body.put("detail", message);
    System.err.println(body.toPrettyString());
    ByteBufUtil.writeAscii(content, body.toPrettyString());
    ctx.write(new DefaultHttp2DataFrame(content, true).stream(stream));
  }

  private <T> void sendResponse(T response) {
    sendResponse(HttpResponseStatus.OK, response);
  }

  private <T> void sendResponse() {
    DefaultHttp2Headers res = new DefaultHttp2Headers(true);
    res.status(HttpResponseStatus.NO_CONTENT.codeAsText());
    ctx.write(new DefaultHttp2HeadersFrame(res, true).stream(stream));
  }

  private <T> void sendResponse(HttpResponseStatus status, T response) {
    DefaultHttp2Headers res = new DefaultHttp2Headers(true);
    res.status(status.codeAsText()).add(HttpHeaderNames.CONTENT_TYPE, "application/json");
    ctx.write(new DefaultHttp2HeadersFrame(res).stream(stream));
    ByteBuf content = ctx.alloc().buffer();

    try (OutputStream os = new ByteBufOutputStream(content)) {
      mapper.writeValue(os, response);
    }
    catch (IOException e) {
      sendError(HttpResponseStatus.INTERNAL_SERVER_ERROR, "error generating response");
      return;
    }

    ctx.write(new DefaultHttp2DataFrame(content, true).stream(stream));

  }

  private <T> T readValue(Class<T> valueType) {
    if (baos.size() > 0) {
      try {
        return (mapper.readValue(this.baos.toByteArray(), valueType));
      }
      catch (IOException e) {
        // TODO Auto-generated catch block
        throw new RuntimeException(e);
      }
    }
    try {
      return valueType.getConstructor().newInstance();
    }
    catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
           | SecurityException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

  //

  private void process(OpenStream value) {
    log.info("processing {}", value);
    this.bus.post(value);
    sendResponse();
  }

}
