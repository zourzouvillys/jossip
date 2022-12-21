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

  public static SipResponseFrame createResponse(final SipRequestFrame request, final SipStatusCodes status) {
    return createResponse(request, status, List.of());
  }

  public static SipResponseFrame createResponse(final SipRequestFrame request, final SipStatusCodes status, final Iterable<SipHeaderLine> append) {
    return createResponse(request, status, append, Optional.empty());
  }

  public static
      SipResponseFrame
      createResponse(final SipRequestFrame request, final SipStatusCodes status, final Iterable<SipHeaderLine> append, final Optional<String> body) {

    final Iterable<SipHeaderLine> copies =
      Iterables.filter(
        request.headerLines(),
        h -> h.knownHeaderId().filter(SipFrameUtils::isCopyKind).isPresent());

    return SipResponseFrame.of(
      status,
      Iterables.concat(copies, append),
      body);

  }

  private static boolean isCopyKind(final StandardSipHeaders kind) {
    return switch (kind) {
      case VIA, FROM, TO, CSEQ, CALL_ID -> true;
      default -> false;
    };
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
