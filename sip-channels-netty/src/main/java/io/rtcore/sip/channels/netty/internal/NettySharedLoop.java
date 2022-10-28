package io.rtcore.sip.channels.netty.internal;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public final class NettySharedLoop {

  private NettySharedLoop() {
  }

  // todo: this is a hack, fix.
  private static final RefCounted<NioEventLoopGroup> defaultEventLoopGroup =
    RefCounted.create(
      NioEventLoopGroup::new,
      NioEventLoopGroup::shutdownGracefully);

  public static EventLoopGroup allocate() {
    return defaultEventLoopGroup.get();
  }

  public static void release(final EventLoopGroup group) {
    defaultEventLoopGroup.release((NioEventLoopGroup) group);
  }

}
