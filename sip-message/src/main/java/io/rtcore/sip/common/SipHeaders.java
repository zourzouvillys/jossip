package io.rtcore.sip.common;

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

  };

  /**
   * which headers are in this set. no order guaruntee.
   */

  Stream<SipHeaderId> headers();

  /**
   * fetches a stream of occurrences of the specified header field value. each one will be in it's
   * smallest value - e.g a token set will contain one value per token.
   */

  Stream<String> get(SipHeaderId header);

  static SipHeaders emptyHeaders() {
    return EMPTY_HEADERS;
  }

}
