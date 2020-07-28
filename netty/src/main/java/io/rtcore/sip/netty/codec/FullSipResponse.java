package io.rtcore.sip.netty.codec;


import io.netty.buffer.ByteBuf;

public interface FullSipResponse extends SipResponse, FullSipMessage {

  @Override
  FullSipResponse copy();

  @Override
  FullSipResponse duplicate();

  @Override
  FullSipResponse retainedDuplicate();

  @Override
  FullSipResponse replace(ByteBuf content);

  @Override
  FullSipResponse retain(int increment);

  @Override
  FullSipResponse retain();

  @Override
  FullSipResponse touch();

  @Override
  FullSipResponse touch(Object hint);

  // @Override
  // FullHttpResponse setProtocolVersion(SipVersion version);
  //
  // @Override
  // FullHttpResponse setStatus(SipResponseStatus status);
}
