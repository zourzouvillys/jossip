package io.rtcore.sip.netty.codec;


import io.netty.buffer.ByteBuf;

public interface FullSipMessage extends SipMessage, LastSipContent {
  
  @Override
  FullSipMessage copy();

  @Override
  FullSipMessage duplicate();

  @Override
  FullSipMessage retainedDuplicate();

  @Override
  FullSipMessage replace(ByteBuf content);

  @Override
  FullSipMessage retain(int increment);

  @Override
  FullSipMessage retain();

  @Override
  FullSipMessage touch();

  @Override
  FullSipMessage touch(Object hint);

}
