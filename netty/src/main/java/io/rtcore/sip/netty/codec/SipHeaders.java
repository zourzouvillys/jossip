package io.rtcore.sip.netty.codec;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.Headers;

public interface SipHeaders extends Iterable<Map.Entry<CharSequence, CharSequence>> {

  void add(CharSequence name, CharSequence value);

  CharSequence get(CharSequence contentLength);

  void set(SipHeaders headers);

  void set(CharSequence name, CharSequence value);

  void addAll(CharSequence name, Iterable<? extends CharSequence> values);

  List<CharSequence> getAll(CharSequence header);

  boolean contains(CharSequence contentLength);

  SipHeaders copy();

  void encode(ByteBuf buf);

  Set<CharSequence> names();

  Headers<? extends CharSequence, ? extends CharSequence, ?> asHeaders();

  void set(CharSequence name, List<CharSequence> values);

  default String getString(CharSequence name, String defaultValue) {
    CharSequence val = get(name);
    if (val == null) {
      return defaultValue;
    }
    return val.toString();
  }

  default OptionalInt getInt(CharSequence name) {
    CharSequence val = get(name);
    if (val == null) {
      return OptionalInt.empty();
    }
    return OptionalInt.of(Integer.parseInt(val.toString(), 0, val.length(), 10));
  }

}
