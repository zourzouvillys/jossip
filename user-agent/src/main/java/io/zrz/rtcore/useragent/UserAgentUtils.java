package io.zrz.rtcore.useragent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.NameAddrParser;
import io.rtcore.sip.message.processor.uri.SipUriExtractor;
import io.rtcore.sip.message.uri.SipUri;

public class UserAgentUtils {

  public static Optional<SipUri> extractNextHop(List<SipHeaderLine> headerLines) {

    List<NameAddr> rr =
      headerLines(headerLines.stream(), StandardSipHeaders.RECORD_ROUTE)
        .flatMap(line -> NameAddrParser.parseList(line).stream())
        .collect(Collectors.toList());

    if (rr.isEmpty()) {
      return singleSipContact(headerLines);
    }

    // null (i.e not a SIP URI) should fail.
    return Optional.of(rr.get(rr.size() - 1).address().apply(SipUriExtractor.getInstance()));

  }

  private static Stream<String> headerLines(Stream<SipHeaderLine> headerLines, StandardSipHeaders header) {
    return headerLines
      .filter(line -> line.headerId() == header)
      .map(line -> line.headerValues());
  }

  public static String singleOrThrow(List<SipHeaderLine> headerLines, StandardSipHeaders headerName) {
    List<String> items =
      headerLines(headerLines.stream(), headerName)
        .collect(Collectors.toList());
    if (items.size() != 1) {
      throw new IllegalArgumentException();
    }
    return items.get(0);
  }

  public static Optional<SipUri> singleSipContact(List<SipHeaderLine> headerLines) {
    return contacts(headerLines)
      .map(NameAddr::address)
      .map(uri -> Optional.ofNullable(uri.apply(SipUriExtractor.getInstance())))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .findFirst();
  }

  private static Stream<NameAddr> contacts(List<SipHeaderLine> headerLines) {
    return headerLines
      .stream()
      .filter(l -> StandardSipHeaders.CONTACT.equals(l.headerId()))
      .map(SipHeaderLine::headerValues)
      .map(NameAddrParser::parse);
  }

  private static Stream<NameAddr> nameAddrs(List<SipHeaderLine> headerLines) {
    return headerLines
      .stream()
      .filter(l -> StandardSipHeaders.CONTACT.equals(l.headerId()))
      .map(SipHeaderLine::headerValues)
      .map(NameAddrParser::parse);
  }

}
