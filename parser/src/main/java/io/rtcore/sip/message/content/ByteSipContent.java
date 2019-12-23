package io.rtcore.sip.message.content;

import java.nio.ByteBuffer;

import org.immutables.value.Value;

import io.rtcore.sip.message.message.api.ContentDisposition;
import io.rtcore.sip.message.message.api.headers.MIMEType;

@Value.Immutable
public interface ByteSipContent extends SipContent {

  @Value.Parameter
  MIMEType type();

  @Value.Parameter
  ContentDisposition disposition();

  @Value.Parameter
  ByteBuffer content();

}
