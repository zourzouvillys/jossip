package io.rtcore.sip.channels.netty.tcp;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Iterables.filter;
import static io.rtcore.sip.common.iana.StandardSipHeaders.VIA;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Predicate;

import io.rtcore.sip.channels.connection.SipResponseFrame;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.StandardSipHeaders;

public class IncomingSipVias {

  private final List<SipHeaderLine> values;

  public IncomingSipVias(List<SipHeaderLine> headers) {

    List<SipHeaderLine> present =
      headers
        .stream()
        .filter(isHeader(VIA))
        .collect(Collectors.toList());

    // TODO: modify if it contains rport, add received?

    this.values = present;

  }

  public SipResponseFrame apply(SipResponseFrame response) {
    ArrayList<SipHeaderLine> hdr = new ArrayList<>();
    hdr.addAll(this.values);
    addAll(hdr, filter(response.headerLines(), not(isHeader(VIA))));
    return response.withHeaderLines(hdr);
  }

  private static Predicate<SipHeaderLine> isHeader(StandardSipHeaders type) {
    return h -> h.knownHeaderId().filter(e -> e == type).isPresent();
  }

  public Optional<String> topLine() {
    return this.values.stream().findFirst().map(SipHeaderLine::headerValues);
  }

}
