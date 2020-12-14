package io.rtcore.sip.proxy.http.netty;

import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Future;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableSubscriber;

/**
 * provides a mapping from a Flowable to a Channel, ensuring flow control - data will only be
 * requested while the channel is writable.
 */

public final class ChannelStreamWriter<T> extends ChannelInboundHandlerAdapter implements FlowableSubscriber<T> {

  private static final Logger log = LoggerFactory.getLogger(ChannelStreamWriter.class);

  // the destination channel.
  private final Channel destch;

  // the subscription.
  private Subscription sub;

  /**
   * 
   */

  private ChannelStreamWriter(Channel destch) {
    this.destch = destch;
    this.destch.pipeline().addLast(this);
    this.destch.closeFuture().addListener(this::channelClosed);
  }

  /**
   * if the channel is closed.
   */

  private void channelClosed(Future<? super Void> f) {
    log.info("WRITE CHANNEL CLOSED");
  }

  @Override
  public void onSubscribe(@NonNull Subscription s) {
    log.info("WRITE SUBSCRIBE");
    this.sub = s;
    s.request(1);
  }

  @Override
  public void onNext(@NonNull T t) {
    log.info("WRITE NEXT");
    sub.request(1);
  }

  @Override
  public void onError(Throwable t) {
    log.info("WRITE ERROR: {}", t.getMessage(), t);
  }

  @Override
  public void onComplete() {
    log.info("WRITE COMPLETE");
  }

  public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
    log.info("WRITABILITY CHANGED");
    ctx.fireChannelWritabilityChanged();
  }

  /**
   * once we are initially synchronized and added to the stream. note that the stream could be in
   * any state, we need to query it to decide what to do next.
   */

  @Override
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    log.info("HANDLER ADDED");
  }

  /**
   * removed from the pipeline. should be nothing to do
   */

  @Override
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    log.info("HANDLER REMOVED");
  }

  /**
   * 
   */

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    log.error("channel write error: {}", cause.getMessage(), cause);
    ctx.fireExceptionCaught(cause);
  }

  /**
   * transfer the specified flowable to the destination.
   * 
   * @param source
   * @param destination
   */

  public static final <T> void transfer(Flowable<T> source, Channel destination) {
    source.subscribe(new ChannelStreamWriter<>(destination));
  }

}
