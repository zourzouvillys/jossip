package io.rtcore.sip.common;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import io.rtcore.sip.common.iana.SipHeaderId;

public interface SipHeaders {

  SipHeaders EMPTY_HEADERS = new SipHeaders() {

    @Override
    public Stream<SipHeaderId> headers() {
      return Stream.of();
    }

    @Override
    public Stream<String> get(final SipHeaderId header) {
      return Stream.of();
    }

    @Override
    public List<SipHeaderLine> lines() {
      return List.of();
    }

  };

  /**
   * which headers are in this set. no order guaruntee.
   */

  default Stream<SipHeaderId> headers() {
    return this.lines().stream().map(SipHeaderLine::headerId).distinct();
  }

  /**
   * fetches a stream of occurrences of the specified header field value. each one will be in it's
   * smallest value - e.g a token set will contain one value per token.
   */

  default Stream<String> get(final SipHeaderId header) {
    return this.lines().stream().filter(e -> header.equals(e.headerId())).map(SipHeaderLine::headerValues);
  }

  /**
   * returns an unmodifiable set of the header lines.
   */

  List<SipHeaderLine> lines();

  static SipHeaders emptyHeaders() {
    return EMPTY_HEADERS;
  }

  static SipHeaders of(final Collection<SipHeaderLine> in) {

    final List<SipHeaderLine> headers = List.copyOf(in);

    return () -> headers;

  }

  default Optional<String> firstValue(final SipHeaderId header) {
    return this.lines()
      .stream()
      .filter(e -> header.equals(e.headerId()))
      .map(SipHeaderLine::headerValues)
      .findAny();
  }

}
