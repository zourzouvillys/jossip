package io.rtcore.sip.common;

import java.util.List;
import java.util.stream.Stream;

import io.rtcore.sip.common.iana.SipHeaderId;

public interface SipHeaders {

  final SipHeaders EMPTY_HEADERS = new SipHeaders() {

    @Override
    public Stream<SipHeaderId> headers() {
      return Stream.of();
    }

    @Override
    public Stream<String> get(SipHeaderId header) {
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
    return lines().stream().map(SipHeaderLine::headerId).distinct();
  }

  /**
   * fetches a stream of occurrences of the specified header field value. each one will be in it's
   * smallest value - e.g a token set will contain one value per token.
   */

  default Stream<String> get(SipHeaderId header) {
    return lines().stream().filter(e -> header.equals(e.headerId())).map(SipHeaderLine::headerValues);
  }

  /**
   * returns an unmodifiable set of the header lines.
   */

  List<SipHeaderLine> lines();

  static SipHeaders emptyHeaders() {
    return EMPTY_HEADERS;
  }

  static SipHeaders of(List<SipHeaderLine> in) {

    List<SipHeaderLine> headers = List.copyOf(in);

    return new SipHeaders() {

      @Override
      public List<SipHeaderLine> lines() {
        return headers;
      }

    };

  }

}
