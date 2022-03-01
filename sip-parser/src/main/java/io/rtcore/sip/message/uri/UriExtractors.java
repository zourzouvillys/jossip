package io.rtcore.sip.message.uri;

import java.util.Optional;

import io.rtcore.sip.common.Host;

public final class UriExtractors {

  private UriExtractors() {
  }

  private static final UriVisitor<Optional<Host>> hostExtractor = new SipUriVisitor<Optional<Host>>() {

    @Override
    public Optional<Host> visit(SipUri uri) {
      return Optional.of(uri.host());
    }

    @Override
    public Optional<Host> visit(Uri unknown) {
      return Optional.empty();
    }

  };

  /**
   * if it's a SIP URI, returns the host.
   * 
   * @return
   */

  public static final UriVisitor<Optional<Host>> hostExtractor() {
    return hostExtractor;
  }

}
