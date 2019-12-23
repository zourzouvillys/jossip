package io.rtcore.sip.generator;

import static java.lang.Long.toHexString;
import static java.util.concurrent.ThreadLocalRandom.current;

import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.message.api.ViaProtocol;
import io.rtcore.sip.message.processor.rfc3261.MutableSipRequest;
import io.rtcore.sip.message.uri.Uri;

public class SipRequestGenerators {

  public static SipRequestGenerator invite(String ruri) {
    return mgr -> invite(mgr.parseUri(ruri)).generate(mgr);
  }

  public static SipRequestGenerator invite(Uri ruri) {
    return mgr -> {
      return MutableSipRequest
        .createInvite(ruri)
        .via(ViaProtocol.TCP, HostAndPort.fromString("localhost"), toHexString(current().nextLong()), false)
        .cseq(1, SipMethod.INVITE)
        .callId(toHexString(current().nextLong()))
        .from(ruri, toHexString(current().nextLong()))
        .to(ruri)
        .contact(ruri)
        .build(mgr);
    };
  }

}
