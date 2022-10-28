package io.rtcore.sip.channels.netty.tcp;

import java.util.concurrent.CompletableFuture;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;

public class NettyUtils {

  public static final CompletableFuture<Channel> toCompletableFuture(ChannelFuture f) {

    CompletableFuture<Channel> channelFuture = new CompletableFuture<>();

    // Acquire from pool and listen for completion
    f.addListener((ChannelFuture future) -> {

      if (future.isSuccess()) {
        channelFuture.complete(future.channel());
      }
      else {
        channelFuture.completeExceptionally(future.cause());
      }

    });

    return channelFuture;
  }

  public static CompletableFuture<Channel> toCompletableFuture(Future<Channel> f) {

    CompletableFuture<Channel> channelFuture = new CompletableFuture<>();

    // Acquire from pool and listen for completion
    f.addListener((Future<Channel> future) -> {

      if (future.isSuccess()) {
        channelFuture.complete(future.get());
      }
      else {
        channelFuture.completeExceptionally(future.cause());
      }

    });

    return channelFuture;
  }

}
