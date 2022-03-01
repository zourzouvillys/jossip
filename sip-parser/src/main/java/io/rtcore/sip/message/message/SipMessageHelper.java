package io.rtcore.sip.message.message;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.io.ByteStreams;

import io.rtcore.sip.common.SipHeaders;
import io.rtcore.sip.common.iana.SipHeaderId;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.content.SipContent;
import io.rtcore.sip.message.processor.rfc3261.DefaultSipResponse;

public class SipMessageHelper {

  public static SipResponse createResponse(SipResponseStatus status, SipRequest request, SipHeaders headers, SipContent content) {
    return new DefaultSipResponse(status, makeResponseHeaders(request, headers, Optional.ofNullable(content)), getBodyBytes(content));
  }

  private static final Set<SipHeaderId> copyIfNotSetSingleHeaders =
    Set.of(
      StandardSipHeaders.TO);

  private static final Set<SipHeaderId> copyAlwaysSingleHeaders =
    Set.of(
      StandardSipHeaders.VIA,
      StandardSipHeaders.FROM,
      StandardSipHeaders.CALL_ID,
      StandardSipHeaders.CSEQ);

  /**
   * 
   * @param req
   * @param headers
   * @param content
   * 
   * @return
   */

  private static List<RawHeader> makeResponseHeaders(SipRequest req, SipHeaders headers, Optional<SipContent> content) {

    Set<SipHeaderId> types = headers.headers().collect(Collectors.toUnmodifiableSet());

    LinkedHashMultimap<SipHeaderId, String> response = LinkedHashMultimap.create();

    for (SipHeaderId hdr : copyAlwaysSingleHeaders) {
      req.headerValues(hdr).forEachOrdered(value -> response.put(hdr, value));
    }

    for (SipHeaderId hdr : types) {
      if (copyAlwaysSingleHeaders.contains(hdr)) {
        // we copy from the original.
        continue;
      }
      headers
        .get(hdr)
        .forEachOrdered(val -> response.put(hdr, val));
    }

    // also add content specific ones.
    if (!response.containsKey(StandardSipHeaders.CONTENT_TYPE)) {
      content.ifPresent(value -> response.put(StandardSipHeaders.CONTENT_TYPE, value.type().toString()));
    }

    for (SipHeaderId hdr : copyIfNotSetSingleHeaders) {
      if (!response.containsKey(hdr)) {
        req.headerValues(hdr).forEachOrdered(value -> response.put(hdr, value));
      }
    }

    return response.entries()
      .stream()
      .map(e -> new RawHeader(e.getKey().prettyName(), e.getValue()))
      .collect(Collectors.toList());

  }

  private static byte[] getBodyBytes(SipContent content) {
    if (content == null) {
      return null;
    }
    try {
      return ByteStreams.toByteArray(content.bufferedReader());
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
