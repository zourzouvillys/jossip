package io.rtcore.sip.message.content;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Iterables;

import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipHeaderId;
import io.rtcore.sip.message.message.SipMessage;
import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.api.ContentDisposition;
import io.rtcore.sip.message.message.api.headers.MIMEType;

public class SipContentUtils {

  public static SipContent create(final SipMessage msg) {
    return ImmutableByteSipContent.of(
      msg.contentType().orElse(MIMEType.APPLICATION_SDP),
      msg.contentDisposition().orElse(ContentDisposition.SessionRequired),
      ByteBuffer.wrap(msg.body()));
  }

  public static SipContent fromResponse(final SipResponse msg) {
    return ImmutableByteSipContent.of(
      msg.contentType().orElse(MIMEType.APPLICATION_SDP),
      msg.contentDisposition().orElse(ContentDisposition.SessionRequired),
      ByteBuffer.wrap(msg.body()));
  }

  /**
   * fetch a single value.
   */

  public static Optional<String> firstValue(final List<SipHeaderLine> headers, final SipHeaderId headerId) {
    for (final SipHeaderLine header : headers) {
      if (header.headerId() == headerId) {
        return Optional.of(header.headerValues());
      }
    }
    return Optional.empty();
  }

  public static Iterable<String> headerValues(final List<SipHeaderLine> headers, final SipHeaderId headerId) {
    return Iterables.transform(
      Iterables.filter(headers, h -> h.headerId() == headerId),
      SipHeaderLine::headerValues);
  }

  public static boolean hasHeader(final List<SipHeaderLine> headers, final SipHeaderId headerId) {
    return headers.stream().anyMatch(h -> h.headerId() == headerId);
  }

}
