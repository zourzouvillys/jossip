package io.rtcore.dns.monitor;

import java.util.Optional;

import org.immutables.value.Value;

import com.google.common.base.Strings;
import com.google.common.primitives.UnsignedInts;

@Value.Immutable
public interface NaptrRecord extends Record {

  // [order] [preference] [flags] [services] [regexp] [replacement]

  int order();

  int preference();

  String flags();

  String service();

  Optional<String> regexp();

  String replacement();

  static NaptrRecord parse(String value) {
    String[] parts = value.split(" ", 6);
    if (parts.length != 6) {
      return null;
    }
    return ImmutableNaptrRecord.builder()
      .order(UnsignedInts.parseUnsignedInt(parts[0]))
      .preference(UnsignedInts.parseUnsignedInt(parts[1]))
      .flags(parts[2])
      .service(parts[3])
      .regexp(Optional.ofNullable(Strings.emptyToNull(parts[4])))
      .replacement(parts[5])
      .build();
  }

}
