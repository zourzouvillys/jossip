package io.rtcore.sip.generator;

import static java.lang.Long.toHexString;
import static java.util.concurrent.ThreadLocalRandom.current;

import com.google.common.net.HostAndPort;

import io.rtcore.sip.common.iana.SipMethodId;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.message.api.ViaProtocol;
import io.rtcore.sip.message.processor.rfc3261.MutableSipRequest;
import io.rtcore.sip.message.uri.SipUri;
import io.rtcore.sip.message.uri.Uri;

public class SipRequestGenerators {

  public static SipRequestGenerator request(final SipMethodId method, final String ruri, final String branchId) {
    return mgr -> request(method, mgr.parseUri(ruri), branchId).generate(mgr);
  }

  public static SipRequestGenerator request(final SipMethodId method, final Uri ruri, final String branchId) {
    return mgr -> MutableSipRequest
      .create(method)
      .via(ViaProtocol.TCP, HostAndPort.fromString("localhost"), branchId, false)
      .cseq(1, method)
      .callId(toHexString(current().nextLong()))
      .from(ruri, toHexString(current().nextLong()))
      .to(ruri)
      .contact(ruri)
      .build(mgr);
  }

  public static SipRequestGenerator invite(final String ruri) {
    return mgr -> invite(mgr.parseUri(ruri)).generate(mgr);
  }

  public static SipRequestGenerator invite(final Uri ruri) {
    return mgr -> MutableSipRequest
      .createInvite(ruri)
      .via(ViaProtocol.TCP, HostAndPort.fromString("localhost"), toHexString(current().nextLong()), false)
      .cseq(1, SipMethod.INVITE)
      .callId(toHexString(current().nextLong()))
      .from(ruri, toHexString(current().nextLong()))
      .to(ruri)
      .contact(ruri)
      .build(mgr);
  }

  public static SipRequestGenerator options(final HostAndPort target) {
    return options(target, 1);
  }

  public static SipRequestGenerator options(final HostAndPort target, final long seq) {
    final SipUri remote = SipUri.create(target);
    final SipUri local = SipUri.create(HostAndPort.fromString("localhost"));
    return mgr -> MutableSipRequest
      .create(SipMethod.OPTIONS, remote)
      .via(ViaProtocol.TCP, local.getHost(), toHexString(current().nextLong()), false)
      .cseq(seq, SipMethod.OPTIONS)
      .callId(toHexString(current().nextLong()))
      .from(local, toHexString(current().nextLong()))
      .to(remote)
      .contact(local)
      .build(mgr);
  }

  public static SipRequestGenerator notify(final HostAndPort target, final long seq) {
    final SipUri remote = SipUri.create(target);
    final SipUri local = SipUri.create(HostAndPort.fromString("localhost"));
    return mgr -> MutableSipRequest
      .create(SipMethod.NOTIFY, remote)
      .via(ViaProtocol.TCP, local.getHost(), toHexString(current().nextLong()), false)
      .cseq(seq, SipMethod.NOTIFY)
      .callId(toHexString(current().nextLong()))
      .from(local, toHexString(current().nextLong()))
      .event("keepalive")
      .to(remote)
      .contact(local)
      .build(mgr);
  }

}
