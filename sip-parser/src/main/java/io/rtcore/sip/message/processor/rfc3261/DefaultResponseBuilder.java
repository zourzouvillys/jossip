package io.rtcore.sip.message.processor.rfc3261;

import java.time.Duration;
import java.util.List;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.SipResponseStatus;
import io.rtcore.sip.message.processor.rfc3261.message.api.ResponseBuilder;

/**
 * A {@link ResponseBuilder} which always sends a provided status code.
 *
 *
 */
public class DefaultResponseBuilder implements ResponseBuilder {
  private SipResponseStatus status;
  private final List<RawHeader> headers = Lists.newLinkedList();
  private final RfcSipMessageManager manager;

  /**
   * Constructs a {@link ResponseBuilder} instance which returns the given status code.
   */

  public DefaultResponseBuilder() {
    this.manager = new RfcSipMessageManagerBuilder().build();
    this.status = SipResponseStatus.OK;
  }

  public DefaultResponseBuilder(final SipResponseStatus status) {
    this.manager = new RfcSipMessageManagerBuilder().build();
    this.status = status;
  }

  public DefaultResponseBuilder(final RfcSipMessageManager manager, final SipResponseStatus status) {
    this.manager = manager;
    this.status = status;
  }

  // TODO: this is a bit of a hac. fix.
  private final String[] copy = { "From", "Call-ID", "CSeq", "Via", "To", "f", "i", "v", "t" };

  @Override
  public final SipResponse build(final SipRequest req, final SipMessageManager manager) {
    final DefaultSipResponse res = new DefaultSipResponse(this.manager, this.status);
    for (final String hn : this.copy) {
      for (final RawHeader header : req.headers()) {
        if (hn.toLowerCase().equals(header.name().toLowerCase())) {
          // loop, otherwise only a single value gets copied (e.g, multiple Via headers).
          res.addHeader(header);
        }
      }
    }
    res.addHeader(new RawHeader("Content-Length", "0"));
    for (final RawHeader header : this.headers) {
      res.addHeader(header);
    }
    return res;
  }

  @Override
  public SipResponse build(final SipRequest req) {
    return this.build(req, null);
  }

  @Override
  public ResponseBuilder setStatus(final SipResponseStatus status) {
    this.status = status;
    return this;
  }

  @Override
  public final ResponseBuilder addHeader(final RawHeader header) {
    this.headers.add(header);
    return this;
  }

  public ResponseBuilder setExpires(final Duration standardSeconds) {
    this.headers.add(new RawHeader("Expires", Integer.toString((int) standardSeconds.getSeconds())));
    return this;
  }

  @Override
  public SipResponseStatus status() {
    return this.status;
  }
}
