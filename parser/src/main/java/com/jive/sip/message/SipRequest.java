package com.jive.sip.message;

import java.util.List;
import java.util.Optional;

import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.auth.headers.Authorization;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.api.EventSpec;
import com.jive.sip.message.api.NameAddr;
import com.jive.sip.message.api.RAck;
import com.jive.sip.message.api.Reason;
import com.jive.sip.message.api.Replaces;
import com.jive.sip.message.api.SipHeaderDefinition;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.message.api.TargetDialog;
import com.jive.sip.message.api.TokenSet;
import com.jive.sip.message.api.headers.RValue;
import com.jive.sip.uri.Uri;

/**
 * Representation of a SIP request.
 *
 * @author theo
 *
 */
public interface SipRequest extends SipMessage {

  SipMethod getMethod();

  Uri getUri();

  /**
   *
   * @return The Max-Forwards value for this request.
   */

  Optional<UnsignedInteger> getMaxForwards();

  /**
   *
   * @return
   */

  Optional<TokenSet> getProxyRequire();

  /**
   *
   * @return
   */

  List<Authorization> getProxyAuthorization();

  /**
   *
   * @return
   */

  List<Authorization> getAuthorization();

  /**
   * @return
   */
  Optional<UnsignedInteger> getExpires();

  /**
   *
   * @return
   */
  List<NameAddr> getPath();

  /**
   * @return
   */
  Optional<EventSpec> getEvent();

  /**
   * @return
   */

  Optional<CharSequence> getUserAgent();

  /**
   *
   * @return
   */

  Optional<Replaces> getReplaces();

  Optional<NameAddr> getReferTo();

  Optional<NameAddr> getReferredBy();

  /**
   * Returns the disposition tokens provided in the request.
   *
   * @return
   */

  Optional<TokenSet> getRequestDisposition();

  /**
   *
   * @return
   */

  Optional<TokenSet> getPrivacy();

  /**
   * The Resource-Priority header.
   */

  List<RValue> getResourcePriority();

  /**
   *
   * @param headers
   * @return
   */

  SipMessage withReplacedHeaders(final RawHeader... headers);

  SipRequest withUri(final Uri Uri);

  SipRequest withMethod(final SipMethod cancel);

  SipRequest withBody(final byte[] body);

  SipRequest withBody(final String body);

  @Override
  SipRequest withoutHeaders(final String... headerNames);

  @Override
  SipRequest withoutHeaders(final SipHeaderDefinition... headers);

  /**
   * Prepends a field to a collection header (e.g, one which is List<T>).
   *
   * @param string
   *          The header name
   * @param field
   *          The header field value, in parsed form.
   *
   * @return
   */
  @Override
  SipRequest withPrepended(final String name, final Object field);

  @Override
  SipRequest withAppended(final String name, final Object field);

  <T> SipRequest withParsed(final String name, final List<T> fields);

  SipRequest withPrepended(final RawHeader raw);

  Optional<NameAddr> getPServedUser();

  /**
   *
   */

  Optional<RAck> getRAck();

  /**
   * Each of the P-Asseted-Identity values in the SIP-Request.
   *
   * If there is no headers, an empty immutable list will be returned.
   *
   * @return
   */

  List<NameAddr> getPAssertedIdentity();

  Optional<Reason> getReason();

  @Override
  SipRequest withFrom(final NameAddr na);

  @Override
  SipRequest withTo(final NameAddr na);

  SipRequest withRoute(final List<NameAddr> routeSet);

  SipRequest withContact(final NameAddr local);

  SipRequest withContact(final Uri local);

  @Override
  SipRequest withCSeq(final long seqNum, final SipMethod method);

  @Override
  SipRequest withIncrementedCSeq(final SipMethod method);

  Optional<TargetDialog> getTargetDialog();

}
