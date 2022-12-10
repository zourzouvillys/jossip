package io.rtcore.gateway.engine;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.SipHeaders;
import io.rtcore.sip.common.iana.SipHeaderId;

public class SipHeaderMultimap {

  // note: we retain this map and don't expose.
  private final Multimap<SipHeaderId, String> headers;

  public SipHeaderMultimap(final Collection<SipHeaderLine> headerLines) {
    this.headers =
      headerLines.stream()
        .collect(Multimaps.toMultimap(e -> e.headerId(), e -> e.headerValues(), () -> LinkedHashMultimap.create()));
  }

  public boolean contains(final SipHeaderId h) {
    return this.headers.containsKey(h);
  }

  public void putIfAbsent(final SipHeaderId h, final String value) {
    this.computeIfAbsent(h, k -> value);
  }

  public void computeIfAbsent(final SipHeaderId h, final Function<SipHeaderId, String> value) {
    if (!this.headers.containsKey(h)) {
      final String nextValue = value.apply(h);
      if (nextValue != null) {
        this.headers.put(h, nextValue);
      }
    }
  }

  /**
   * creates a new list of all the header lines. the caller can modify.
   */

  public List<SipHeaderLine> toHeaderLines() {
    return this.headers.entries()
      .stream()
      .map(e -> SipHeaderLine.of(e.getKey(), e.getValue()))
      .collect(Collectors.toList());
  }

  /**
   * creates an immutable set of the headers.
   */

  public SipHeaders toSipHeaders() {
    final List<SipHeaderLine> items =
      this.headers.entries()
        .stream()
        .map(e -> SipHeaderLine.of(e.getKey(), e.getValue()))
        .collect(Collectors.toUnmodifiableList());
    return SipHeaders.of(items);
  }

  public static SipHeaderMultimap from(final List<SipHeaderLine> headerLines) {
    return new SipHeaderMultimap(headerLines);
  }

  /**
   * returns a single value, if it exists and has a single value. throws if there is more than one
   * value.
   */

  public Optional<String> singleValue(final SipHeaderId headerId) {

    final Collection<String> values = this.headers.get(headerId);

    if (values.size() != 1) {
      throw new IllegalArgumentException();
    }

    return Optional.of(values.iterator().next());

  }

}
