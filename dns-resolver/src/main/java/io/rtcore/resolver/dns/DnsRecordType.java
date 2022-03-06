package io.rtcore.resolver.dns;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DnsRecordType {

  A(1),
  NS(2),
  SOA(6),
  MX(15),
  TXT(16),
  
  SIG(24),
  KEY(25),

  AAAA(28),

  SRV(33),
  NAPTR(35),

  DNAME(39),

  DS(43),
  RRSIG(46),
  NSEC(47),
  DNSKEY(48),
  
  NSEC3(50),
  NSEC3PARAM(51),

  ANY(255),

  URI(256),

  ;

  private int i;

  DnsRecordType(int i) {
    this.i = i;
  }

  @JsonValue
  public int value() {
    return this.i;
  }

  private static Map<Integer, DnsRecordType> values = Stream.of(values()).collect(Collectors.toUnmodifiableMap(e -> e.i, e -> e));

  @JsonCreator
  public static DnsRecordType type(int key) {
    return values.get(key);
  }

}
