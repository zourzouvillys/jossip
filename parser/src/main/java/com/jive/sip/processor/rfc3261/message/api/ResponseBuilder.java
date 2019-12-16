package com.jive.sip.processor.rfc3261.message.api;

import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.SipRequest;
import com.jive.sip.message.SipResponse;
import com.jive.sip.message.SipResponseStatus;
import com.jive.sip.processor.rfc3261.SipMessageManager;

/**
 * Interface used to generate a response to a request using a defined set of rules.
 *
 * @author theo
 *
 */

public interface ResponseBuilder {

  /**
   * Adds a header to populate the response.
   *
   * @param header
   *          The {@link RawHeader} instance to add.
   *
   * @return self instance
   */

  ResponseBuilder addHeader(final RawHeader header);

  /**
   *
   * @param req
   *          The {@link SipRequest} instance to build a response for.
   *
   * @param manager
   *          The {@link SipMessageManager} to use for creating the
   *
   * @return The new {@link SipResponse} instance that represents a response to the provided
   *         {@literal req} parameter built using the current {@link ResponseBuilder}.
   *
   */

  SipResponse build(final SipRequest req, final SipMessageManager manager);

  ResponseBuilder setStatus(final SipResponseStatus status);

  SipResponse build(final SipRequest req);

  SipResponseStatus getStatus();

}
