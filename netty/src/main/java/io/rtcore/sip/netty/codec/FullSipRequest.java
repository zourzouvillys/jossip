package io.rtcore.sip.netty.codec;


import io.netty.buffer.ByteBuf;

public interface FullSipRequest extends SipRequest, FullSipMessage {

  @Override
  FullSipRequest copy();

  @Override
  FullSipRequest duplicate();

  @Override
  FullSipRequest retainedDuplicate();

  @Override
  FullSipRequest replace(ByteBuf content);

  @Override
  FullSipRequest retain(int increment);

  @Override
  FullSipRequest retain();

  @Override
  FullSipRequest touch();

  @Override
  FullSipRequest touch(Object hint);

  // @Override
  // FullSipRequest setProtocolVersion(HttpVersion version);
  //
  // @Override
  // FullSipRequest setMethod(HttpMethod method);
  //
  // @Override
  // FullSipRequest setUri(String uri);

}
