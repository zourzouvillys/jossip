package io.rtcore.sip.message.processor.rfc3261.message.api;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.SipResponseStatus;
import io.rtcore.sip.message.processor.rfc3261.SipMessageManager;

/**
 * Interface used to generate a response to a request using a defined set of rules.
 *
 * 
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

  SipResponseStatus status();

}
