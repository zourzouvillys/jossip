package com.jive.sip.processor.rfc3261;

import java.util.Collection;
import java.util.List;

import com.jive.sip.base.api.RawHeader;
import com.jive.sip.base.api.RawMessage;
import com.jive.sip.message.api.NameAddr;
import com.jive.sip.message.api.Reason;
import com.jive.sip.message.api.SipMessage;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.message.api.SipResponseStatus;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.processor.rfc3261.message.api.ResponseBuilder;
import com.jive.sip.uri.SipUri;
import com.jive.sip.uri.Uri;

/**
 * Interface for working with SIP messages.
 */

public interface SipMessageManager {

  /**
   * converts an incoming {@link RawMessage} to a parsed {@link SipMessage}.
   * 
   * note: this only performs minimal syntactic checks, it doesn't do semantic ones (e.g, check
   * status codes).
   *
   * @param msg
   *          the raw message to parse.
   * 
   * @return a parsed sip message.
   */

  SipMessage convert(final RawMessage msg);

  /**
   * 
   * @param status
   * @return
   */

  ResponseBuilder responseBuilder(final SipResponseStatus status);

  <T> T adapt(final Class<T> adapter);

  /**
   * Creates a new INVITE message.
   *
   * @param invite
   * @param ruri
   * @return
   */

  DefaultSipRequest createRequest(final SipMethod method, final Uri ruri, final Collection<RawHeader> headers, final byte[] body);

  /**
   * 
   * @param status
   * @param build
   * @param body
   * @return
   */

  DefaultSipResponse createResponse(final SipResponseStatus status, final List<RawHeader> build, final byte[] body);

  /**
   * Create an ACK for the given response.
   *
   * @param res
   * @return
   */

  SipRequest createAck(final SipResponse res, final List<NameAddr> route);

  /**
   * 
   * @param original
   * @param reason
   * @return
   */

  SipRequest createCancel(final SipRequest original, final Reason reason);

  /**
   * Converts a {@link RawMessage}, optionally parsing all known headers up front.
   *
   * @param raw
   * @param lazy
   *          if false, parses all headers up front.
   * @return
   *
   */

  SipMessage convert(final RawMessage raw, final boolean lazy);

  /**
   * Creates a SIP request from a SIP URI.
   *
   * 'method' is INVITE, unless there is a 'method' param.
   *
   * All embedded headers are parsed as headers in the message.
   *
   * The R-URI is whatever is left over.
   *
   * @param target
   *
   * @return
   *
   */

  SipRequest fromUri(final SipUri target, SipMethod defaultMethod);

  /**
   * Parse a URI.
   */

  Uri parseUri(final String uri);

  /**
   * Parse a Name-Addr
   *
   * @param na
   * @return
   */

  NameAddr parseNameAddr(final String na);

  /**
   * parses parameters like ";a=z;b=1;d="xxx"
   */

  Parameters parseParameters(final String params);

  static SipMessageManager defaultManager() {
    return RfcSipMessageManager.defaultInstance();
  }

}
