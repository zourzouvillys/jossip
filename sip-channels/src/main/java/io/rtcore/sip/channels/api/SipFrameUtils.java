package io.rtcore.sip.channels.api;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.Iterables;

import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipHeaderId;
import io.rtcore.sip.common.iana.SipStatusCodes;
import io.rtcore.sip.common.iana.StandardSipHeaders;

public final class SipFrameUtils {

  private SipFrameUtils() {
  }

  public static SipResponseFrame createResponse(SipRequestFrame request, SipStatusCodes status) {
    return createResponse(request, status, List.of());
  }

  public static SipResponseFrame createResponse(SipRequestFrame request, SipStatusCodes status, Iterable<SipHeaderLine> append) {

    Iterable<SipHeaderLine> copies =
      Iterables.filter(
        request.headerLines(),
        h -> h.knownHeaderId().filter(kind -> isCopyKind(kind)).isPresent());

    return SipResponseFrame.of(
      status,
      Iterables.concat(copies, append));
  }

  private static boolean isCopyKind(StandardSipHeaders kind) {
    switch (kind) {
      case VIA:
      case FROM:
      case TO:
      case CSEQ:
      case CALL_ID:
        return true;
      default:
        return false;
    }
  }

  /**
   * fetch a single value.
   */

  public static Optional<String> firstValue(List<SipHeaderLine> headers, SipHeaderId headerId) {
    for (SipHeaderLine header : headers) {
      if (header.headerId() == headerId) {
        return Optional.of(header.headerValues());
      }
    }
    return Optional.empty();
  }

  public static Iterable<String> headerValues(List<SipHeaderLine> headers, SipHeaderId headerId) {
    return Iterables.transform(
      Iterables.filter(headers, h -> h.headerId() == headerId),
      SipHeaderLine::headerValues);
  }

}
