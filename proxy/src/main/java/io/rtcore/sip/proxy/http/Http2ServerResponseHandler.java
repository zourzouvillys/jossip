package io.rtcore.sip.proxy.http;

import java.util.HashMap;
import java.util.function.Consumer;

import com.google.common.eventbus.EventBus;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.DefaultHttp2WindowUpdateFrame;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2FrameStreamEvent;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2ResetFrame;
import io.netty.handler.codec.http2.Http2StreamFrame;
import io.netty.handler.codec.http2.Http2WindowUpdateFrame;

/**
 * handle HTTP requests.
 */

final class Http2ServerResponseHandler extends ChannelDuplexHandler {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Http2ServerResponseHandler.class);

  private final EventBus bus;
  private final HashMap<Integer, IncomingRequestStream> streams = new HashMap<>();

  Http2ServerResponseHandler(EventBus bus) {
    this.bus = bus;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof Http2StreamFrame) {
      channelRead0(ctx, (Http2StreamFrame) msg);
    }
    else {
      super.channelRead(ctx, msg);
    }
  }

  private void channelRead0(ChannelHandlerContext ctx, Http2StreamFrame msg) throws Exception {
    log.info("got message {}", msg);
    if (msg instanceof Http2HeadersFrame) {
      channelRead1(ctx, (Http2HeadersFrame) msg);
    }
    else if (msg instanceof Http2DataFrame) {
      channelRead1(ctx, (Http2DataFrame) msg);
    }
    else if (msg instanceof Http2ResetFrame) {
      channelRead1(ctx, (Http2ResetFrame) msg);
    }
    else if (msg instanceof Http2WindowUpdateFrame) {
      channelRead1(ctx, (Http2WindowUpdateFrame) msg);
    }
    else {
      super.channelRead(ctx, msg);
    }
  }

  private void channelRead1(ChannelHandlerContext ctx, Http2HeadersFrame headersFrame) throws Http2Exception {

    Http2FrameStream stream = headersFrame.stream();

    int id =
      stream == null ? 0
                     : stream.id();

    IncomingRequestStream state = this.streams.computeIfAbsent(id, _id -> new IncomingRequestStream(bus, ctx, stream));

    if (state != null) {
      state.headers(headersFrame.headers(), headersFrame.isEndStream());
    }

  }

  private void channelRead1(ChannelHandlerContext ctx, Http2DataFrame data) {
    applyStream(data.stream(), strm -> strm.data(data.content(), data.isEndStream()));
    ctx.write(new DefaultHttp2WindowUpdateFrame(data.initialFlowControlledBytes()).stream(data.stream()));
  }

  private void channelRead1(ChannelHandlerContext ctx, Http2WindowUpdateFrame msg) {
    applyStream(msg.stream(), strm -> strm.windowUpdate(msg.windowSizeIncrement()));
  }

  private void channelRead1(ChannelHandlerContext ctx, Http2ResetFrame msg) {
    applyStream(msg.stream(), IncomingRequestStream::reset);
  }

  private void applyStream(Http2FrameStream stream, Consumer<IncomingRequestStream> consumer) {

    if (stream == null) {
      return;
    }

    IncomingRequestStream h = this.streams.get(stream.id());

    if (h != null) {
      consumer.accept(h);
    }

  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof Http2FrameStreamEvent) {
      // ignore, as these are handled by the http2 processor.
      log.debug("http2 frame event {}", ((Http2FrameStreamEvent) evt).type());
    }
    else {
      log.debug("user event {}", evt);
    }
    super.userEventTriggered(ctx, evt);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    log.warn("error processing http2 stream: {}", cause.getMessage(), cause);
    super.exceptionCaught(ctx, cause);
  }

}
