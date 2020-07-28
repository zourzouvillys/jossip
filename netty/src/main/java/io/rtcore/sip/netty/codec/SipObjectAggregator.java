package io.rtcore.sip.netty.codec;

import static io.rtcore.sip.netty.codec.SipUtil.getContentLength;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageAggregator;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class SipObjectAggregator extends MessageAggregator<SipObject, SipMessage, SipContent, FullSipMessage> {

  private static final InternalLogger logger = InternalLoggerFactory.getInstance(SipObjectAggregator.class);

  private final boolean closeOnExpectationFailed;

  /**
   * Creates a new instance.
   * 
   * @param maxContentLength
   *          the maximum length of the aggregated content in bytes. If the length of the aggregated
   *          content exceeds this value,
   *          {@link #handleOversizedMessage(ChannelHandlerContext, SipMessage)} will be called.
   */
  
  public SipObjectAggregator(int maxContentLength) {
    this(maxContentLength, false);
  }

  /**
   * Creates a new instance.
   * 
   * @param maxContentLength
   *          the maximum length of the aggregated content in bytes. If the length of the aggregated
   *          content exceeds this value,
   *          {@link #handleOversizedMessage(ChannelHandlerContext, SipMessage)} will be called.
   * @param closeOnExpectationFailed
   *          If a 100-continue response is detected but the content length is too large then
   *          {@code true} means close the connection. otherwise the connection will remain open and
   *          data will be consumed and discarded until the next request is received.
   */
  public SipObjectAggregator(int maxContentLength, boolean closeOnExpectationFailed) {
    super(maxContentLength);
    this.closeOnExpectationFailed = closeOnExpectationFailed;
  }

  @Override
  protected boolean isStartMessage(SipObject msg) throws Exception {
    return msg instanceof SipMessage;
  }

  @Override
  protected boolean isContentMessage(SipObject msg) throws Exception {
    return msg instanceof SipContent;
  }

  @Override
  protected boolean isLastContentMessage(SipContent msg) throws Exception {
    return msg instanceof LastSipContent;
  }

  @Override
  protected boolean isAggregated(SipObject msg) throws Exception {
    return msg instanceof FullSipMessage;
  }

  @Override
  protected boolean isContentLengthInvalid(SipMessage start, int maxContentLength) {
    try {
      return getContentLength(start, -1L) > maxContentLength;
    }
    catch (final NumberFormatException e) {
      return false;
    }
  }

  @Override
  protected Object newContinueResponse(SipMessage start, int maxContentLength, ChannelPipeline pipeline) {
    return null;
  }

  @Override
  protected boolean closeAfterContinueResponse(Object msg) {
    return closeOnExpectationFailed && ignoreContentAfterContinueResponse(msg);
  }

  @Override
  protected boolean ignoreContentAfterContinueResponse(Object msg) {
    if (msg instanceof SipResponse) {
      final SipResponse SipResponse = (SipResponse) msg;
      return SipResponse.status().codeClass().equals(SipStatusClass.CLIENT_ERROR);
    }
    return false;
  }

  @Override
  protected FullSipMessage beginAggregation(SipMessage start, ByteBuf content) throws Exception {

    assert !(start instanceof FullSipMessage);

    AggregatedFullSipMessage ret;

    if (start instanceof SipRequest) {
      ret = new AggregatedFullSipRequest((SipRequest) start, content, null);
    }
    else if (start instanceof SipResponse) {
      ret = new AggregatedFullSipResponse((SipResponse) start, content, null);
    }
    else {
      throw new Error();
    }

    return ret;

  }

  @Override
  protected void aggregate(FullSipMessage aggregated, SipContent content) throws Exception {
    if (content instanceof LastSipContent) {
      // Merge trailing headers into the message.
      ((AggregatedFullSipMessage) aggregated).setTrailingHeaders(((LastSipContent) content).trailingHeaders());
    }
  }

  @Override
  protected void finishAggregation(FullSipMessage aggregated) throws Exception {
    // Set the 'Content-Length' header. If one isn't already set.
    // This is important as HEAD responses will use a 'Content-Length' header which
    // does not match the actual body, but the number of bytes that would be
    // transmitted if a GET would have been used.
    //
    // See rfc2616 14.13 Content-Length

    if (!SipUtil.isContentLengthSet(aggregated)) {
      aggregated.headers()
        .set(
          SipHeaderNames.CONTENT_LENGTH,
          String.valueOf(aggregated.content().readableBytes()));
    }

  }

  @Override
  protected void handleOversizedMessage(final ChannelHandlerContext ctx, SipMessage oversized) throws Exception {
    if (oversized instanceof SipRequest) {
      // send back a 413 and close the connection

      // If the client started to send data already, close because it's impossible to recover.
      // If keep-alive is off and 'Expect: 100-continue' is missing, no need to leave the connection
      // open.
      // if ((oversized instanceof FullSipMessage)
      // ||
      // (!SipUtil.is100ContinueExpected(oversized) && !SipUtil.isKeepAlive(oversized))) {
      // ChannelFuture future = ctx.writeAndFlush(TOO_LARGE_CLOSE.retainedDuplicate());
      // future.addListener(new ChannelFutureListener() {
      // @Override
      // public void operationComplete(ChannelFuture future) throws Exception {
      // if (!future.isSuccess()) {
      // logger.debug("Failed to send a 413 Request Entity Too Large.", future.cause());
      // }
      // ctx.close();
      // }
      // });
      // }
      // else {
      // ctx.writeAndFlush(TOO_LARGE.retainedDuplicate()).addListener(new ChannelFutureListener() {
      // @Override
      // public void operationComplete(ChannelFuture future) throws Exception {
      // if (!future.isSuccess()) {
      // logger.debug("Failed to send a 413 Request Entity Too Large.", future.cause());
      // ctx.close();
      // }
      // }
      // });
      // }

      // If an oversized request was handled properly and the connection is still alive
      // (i.e. rejected 100-continue). the decoder should prepare to handle a new message.
      SipObjectDecoder decoder = ctx.pipeline().get(SipObjectDecoder.class);
      if (decoder != null) {
        decoder.reset();
      }
    }
    else if (oversized instanceof SipResponse) {
      ctx.close();
      throw new TooLongFrameException("Response entity too large: " + oversized);
    }
    else {
      throw new IllegalStateException();
    }
  }

  private abstract static class AggregatedFullSipMessage implements FullSipMessage {
    protected final SipMessage message;
    private final ByteBuf content;
    private SipHeaders trailingHeaders;

    AggregatedFullSipMessage(SipMessage message, ByteBuf content, SipHeaders trailingHeaders) {
      this.message = message;
      this.content = content;
      this.trailingHeaders = trailingHeaders;
    }

    @Override
    public SipHeaders trailingHeaders() {
      SipHeaders trailingHeaders = this.trailingHeaders;
      if (trailingHeaders == null) {
        return EmptySipHeaders.INSTANCE;
      }
      else {
        return trailingHeaders;
      }
    }

    void setTrailingHeaders(SipHeaders trailingHeaders) {
      this.trailingHeaders = trailingHeaders;
    }

    @Override
    public SipVersion protocolVersion() {
      return message.protocolVersion();
    }

    @Override
    public SipHeaders headers() {
      return message.headers();
    }

    @Override
    public DecoderResult decoderResult() {
      return message.decoderResult();
    }

    @Override
    public void setDecoderResult(DecoderResult result) {
      message.setDecoderResult(result);
    }

    @Override
    public ByteBuf content() {
      return content;
    }

    @Override
    public int refCnt() {
      return content.refCnt();
    }

    @Override
    public FullSipMessage retain() {
      content.retain();
      return this;
    }

    @Override
    public FullSipMessage retain(int increment) {
      content.retain(increment);
      return this;
    }

    @Override
    public FullSipMessage touch(Object hint) {
      content.touch(hint);
      return this;
    }

    @Override
    public FullSipMessage touch() {
      content.touch();
      return this;
    }

    @Override
    public boolean release() {
      return content.release();
    }

    @Override
    public boolean release(int decrement) {
      return content.release(decrement);
    }

    @Override
    public abstract FullSipMessage copy();

    @Override
    public abstract FullSipMessage duplicate();

    @Override
    public abstract FullSipMessage retainedDuplicate();
  }

  private static final class AggregatedFullSipRequest extends AggregatedFullSipMessage implements FullSipRequest {

    AggregatedFullSipRequest(SipRequest request, ByteBuf content, SipHeaders trailingHeaders) {
      super(request, content, trailingHeaders);
    }

    @Override
    public FullSipRequest copy() {
      return replace(content().copy());
    }

    @Override
    public FullSipRequest duplicate() {
      return replace(content().duplicate());
    }

    @Override
    public FullSipRequest retainedDuplicate() {
      return replace(content().retainedDuplicate());
    }

    @Override
    public FullSipRequest replace(ByteBuf content) {

      DefaultFullSipRequest dup =
        new DefaultFullSipRequest(
          protocolVersion(),
          method(),
          uri(),
          content,
          headers().copy(),
          trailingHeaders().copy());

      dup.setDecoderResult(decoderResult());

      return dup;

    }

    @Override
    public FullSipRequest retain(int increment) {
      super.retain(increment);
      return this;
    }

    @Override
    public FullSipRequest retain() {
      super.retain();
      return this;
    }

    @Override
    public FullSipRequest touch() {
      super.touch();
      return this;
    }

    @Override
    public FullSipRequest touch(Object hint) {
      super.touch(hint);
      return this;
    }

    @Override
    public SipMethod method() {
      return ((SipRequest) message).method();
    }

    @Override
    public String uri() {
      return ((SipRequest) message).uri();
    }

    @Override
    public String toString() {
      return SipMessageUtil.appendFullRequest(new StringBuilder(256), this).toString();
    }
  }

  private static final class AggregatedFullSipResponse extends AggregatedFullSipMessage implements FullSipResponse {

    AggregatedFullSipResponse(SipResponse message, ByteBuf content, SipHeaders trailingHeaders) {
      super(message, content, trailingHeaders);
    }

    @Override
    public FullSipResponse copy() {
      return replace(content().copy());
    }

    @Override
    public FullSipResponse duplicate() {
      return replace(content().duplicate());
    }

    @Override
    public FullSipResponse retainedDuplicate() {
      return replace(content().retainedDuplicate());
    }

    @Override
    public FullSipResponse replace(ByteBuf content) {
      DefaultFullSipResponse dup =
        new DefaultFullSipResponse(
          protocolVersion(),
          status(),
          content,
          headers().copy(),
          trailingHeaders().copy());
      dup.setDecoderResult(decoderResult());
      return dup;
    }

    @Override
    public SipResponseStatus status() {
      return ((SipResponse) message).status();
    }

    @Override
    public FullSipResponse retain(int increment) {
      super.retain(increment);
      return this;
    }

    @Override
    public FullSipResponse retain() {
      super.retain();
      return this;
    }

    @Override
    public FullSipResponse touch(Object hint) {
      super.touch(hint);
      return this;
    }

    @Override
    public FullSipResponse touch() {
      super.touch();
      return this;
    }

    @Override
    public String toString() {
      return SipMessageUtil.appendFullResponse(new StringBuilder(256), this).toString();
    }
  }

}
