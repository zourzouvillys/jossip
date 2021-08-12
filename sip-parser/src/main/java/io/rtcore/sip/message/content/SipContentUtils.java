package io.rtcore.sip.message.content;

import java.nio.ByteBuffer;

import io.rtcore.sip.message.message.SipMessage;
import io.rtcore.sip.message.message.api.ContentDisposition;
import io.rtcore.sip.message.message.api.headers.MIMEType;

public class SipContentUtils {

  public static SipContent create(SipMessage msg) {
    return ImmutableByteSipContent.of(
      msg.contentType().orElse(MIMEType.APPLICATION_SDP),
      msg.contentDisposition().orElse(ContentDisposition.SessionRequired),
      ByteBuffer.wrap(msg.body()));
  }

}
