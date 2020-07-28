package io.rtcore.dns.monitor;

import org.junit.jupiter.api.Test;

import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.message.api.SipProtocol;

class DnsClientTest {

  @Test
  void test() {

    DnsQuery.watch(HostAndPort.fromHost("test.jive.rtcfront.net"), SipProtocol.TLS)
      .blockingForEach(System.err::println);

    DnsQuery.watch(HostAndPort.fromHost("testing.pstn.twilio.com"), SipProtocol.TLS)
      .blockingForEach(System.err::println);

    // DnsClient resolver = new DnsClient();
    // System.err.println(resolver.query(("testing.pstn.twilio.com"),
    // DnsRecordType.A).blockingGet());
    // System.err.println(resolver.query(("_sip._udp.realtime.finfra.net"),
    // DnsRecordType.A).blockingGet());
    // System.err.println(resolver.query(("_sip._udp.realtime.finfra.net"),
    // DnsRecordType.SRV).blockingGet());
    // System.err.println(resolver.query(("_sip._udp.realtime.finfra.net"),
    // DnsRecordType.NAPTR).blockingGet());
    // System.err.println(resolver.query(("realtime.finfra.net"),
    // DnsRecordType.NAPTR).blockingGet());

  }

}
