package io.rtcore.sip.channels.netty.tcp;

import java.io.PrintStream;

import io.rtcore.sip.channels.api.SipFrame;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.SipInitialLine;
import io.rtcore.sip.common.SipInitialLine.RequestLine;
import io.rtcore.sip.common.SipInitialLine.ResponseLine;
import io.rtcore.sip.common.iana.SipStatusCodes;;

public class SipChannelUtils {

  public static void dumpFrame(SipFrame frame) {
    dumpFrame(System.out, frame);
  }

  public static void dumpFrame(PrintStream w, SipFrame frame) {

    if (frame.initialLine() instanceof SipInitialLine.RequestLine req) {
      w.println(toString(req));
    }
    else if (frame.initialLine() instanceof SipInitialLine.ResponseLine res) {
      w.println(toString(res));
    }

    frame.headerLines().forEach(h -> w.println(toString(h)));

    w.println();

    frame.body().ifPresent(body -> {

      w.println(body);

    });

  }

  private static String toString(RequestLine req) {
    return String.format("%s %s SIP/2.0", req.method(), req.uri());
  }

  private static String toString(ResponseLine res) {
    return String.format("SIP/2.0 %s %s", res.code(), res.reason().orElse(SipStatusCodes.defaultReason(res.code())));
  }

  private static String toString(SipHeaderLine h) {
    return String.format("%16s = %s", h.knownHeaderId().map(g -> g.prettyName().toLowerCase()).orElse(h.headerName()), h.headerValues());
  }

}
