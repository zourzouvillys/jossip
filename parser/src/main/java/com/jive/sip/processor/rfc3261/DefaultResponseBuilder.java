package com.jive.sip.processor.rfc3261;

import java.time.Duration;
import java.util.List;

import com.google.common.collect.Lists;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.SipRequest;
import com.jive.sip.message.SipResponse;
import com.jive.sip.message.SipResponseStatus;
import com.jive.sip.processor.rfc3261.message.api.ResponseBuilder;

import lombok.Getter;

/**
 * A {@link ResponseBuilder} which always sends a provided status code.
 * 
 * @author theo
 */

public class DefaultResponseBuilder implements ResponseBuilder {
  @Getter
  private SipResponseStatus status;
  private final List<RawHeader> headers = Lists.newLinkedList();
  private final RfcSipMessageManager manager;

  /**
   * Constructs a {@link ResponseBuilder} instance which returns the given status code.
   * 
   * @param status
   *          The status to send back.
   */
  public DefaultResponseBuilder() {
    this.manager = (RfcSipMessageManager) new RfcSipMessageManagerBuilder().build();
    this.status = SipResponseStatus.OK;
  }

  public DefaultResponseBuilder(final SipResponseStatus status) {
    this.manager = (RfcSipMessageManager) new RfcSipMessageManagerBuilder().build();
    this.status = status;
  }

  public DefaultResponseBuilder(final RfcSipMessageManager manager, final SipResponseStatus status) {
    this.manager = manager;
    this.status = status;
  }

  // TODO: this is a bit of a hac. fix.
  private final String[] copy = new String[] { "From", "Call-ID", "CSeq", "Via", "To", "f", "i", "v", "t" };

  @Override
  public final SipResponse build(final SipRequest req, final SipMessageManager manager) {
    final DefaultSipResponse res = new DefaultSipResponse(this.manager, this.status);

    for (final String hn : this.copy) {
      for (final RawHeader header : req.getHeaders()) {
        if (hn.toLowerCase().equals(header.getName().toLowerCase())) {
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
    return build(req, null);
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

}
