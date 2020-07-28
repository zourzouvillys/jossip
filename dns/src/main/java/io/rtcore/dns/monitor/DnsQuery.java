package io.rtcore.dns.monitor;

import com.google.common.net.HostAndPort;

import io.reactivex.rxjava3.core.Flowable;
import io.rtcore.dns.monitor.DnsClient.DnsRecordType;
import io.rtcore.sip.message.message.api.SipProtocol;

public final class DnsQuery {

  private static final DnsClient defaultClient = new DnsClient();

  private DnsQuery() {
    // TODO Auto-generated constructor stub
  }

  public static Flowable<? extends Object> watch(HostAndPort target, SipProtocol protocol) {

    return defaultClient.query(

      String.format(
        "_%s._%s.%s",
        protocol == SipProtocol.TLS ? "sips"
                                    : "sip",
        protocol == SipProtocol.UDP ? "udp"
                                    : "tcp",
        target.getHost()),

      DnsRecordType.SRV)

      .toFlowable()
      .flatMapIterable(e -> e)
      .cast(SrvRecord.class)
      .map(r -> r.server());

    // return defaultClient.query(target.getHost(), DnsRecordType.A)
    // .toFlowable();
    // return defaultClient.query(target.getHost(), DnsRecordType.A)
    // .toFlowable();
  }

}
