package io.rtcore.sip.channels.netty.codec;

import com.google.common.collect.Iterables;

import io.rtcore.sip.channels.connection.SipRequestFrame;
import io.rtcore.sip.channels.connection.SipResponseFrame;
import io.rtcore.sip.common.iana.SipStatusCodes;
import io.rtcore.sip.common.iana.StandardSipHeaders;

public class SipFrameUtils {

  public static SipResponseFrame createResponse(SipRequestFrame request, SipStatusCodes status) {
    return SipResponseFrame.of(status, Iterables.filter(request.headerLines(), h -> h.knownHeaderId().filter(kind -> isCopyKind(kind)).isPresent()));
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

}
