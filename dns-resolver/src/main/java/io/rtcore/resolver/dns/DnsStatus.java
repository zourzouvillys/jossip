package io.rtcore.resolver.dns;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DnsStatus {

  // 0 NoError No Error [RFC1035]
  NoError(0),

  // 1 FormErr Format Error [RFC1035]
  FormErr(1),
  // 2 ServFail Server Failure [RFC1035]
  ServFail(2),

  // 3 NXDomain Non-Existent Domain [RFC1035]
  NXDomain(3),

  // 4 NotImp Not Implemented [RFC1035]
  NotImp(4),

  // 5 Refused Query Refused [RFC1035]
  Refused(5),

  // 6 YXDomain Name Exists when it should not [RFC2136][RFC6672]
  YXDomain(6),

  // 7 YXRRSet RR Set Exists when it should not [RFC2136]
  YXRRSet(7),

  // 8 NXRRSet RR Set that should exist does not [RFC2136]
  NXRRSet(8),

  // 9 NotAuth Server Not Authoritative for zone [RFC2136]
  // 9 NotAuth Not Authorized [RFC8945]
  NotAuth(9),

  // 10 NotZone Name not contained in zone [RFC2136]
  NotZone(10),

  // 11 DSOTYPENI DSO-TYPE Not Implemented [RFC8490]
  DSOTYPENI(11),

  // 12-15 Unassigned
  // 16 BADVERS Bad OPT Version [RFC6891]
  // 16 BADSIG TSIG Signature Failure [RFC8945]
  BADSIG(16),

  // 17 BADKEY Key not recognized [RFC8945]
  BADKEY(17),

  // 18 BADTIME Signature out of time window [RFC8945]
  BADTIME(18),

  // 19 BADMODE Bad TKEY Mode [RFC2930]
  BADMODE(19),

  // 20 BADNAME Duplicate key name [RFC2930]
  BADNAME(20),
  // 21 BADALG Algorithm not supported [RFC2930]
  BADALG(21),

  // 22 BADTRUNC Bad Truncation [RFC8945]
  BADTRUNC(22),

  // 23 BADCOOKIE Bad/missing Server Cookie [RFC7873]
  BADCOOKIE(23),

  ;

  private int i;

  DnsStatus(int i) {
    this.i = i;
  }

  @JsonValue
  public int value() {
    return this.i;
  }

  private static Map<Integer, DnsStatus> values = Stream.of(values()).collect(Collectors.toUnmodifiableMap(e -> e.i, e -> e));

  @JsonCreator
  public static DnsStatus type(int key) {
    return values.get(key);
  }

}
