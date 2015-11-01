package com.jive.sip.message.api;

import java.util.List;
import java.util.Optional;

import com.jive.sip.auth.headers.Authorization;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.api.headers.RValue;
import com.jive.sip.uri.api.Uri;

/**
 * Representation of a SIP response.
 *
 * @author theo
 *
 */
public interface SipResponse extends SipMessage
{

  SipResponseStatus getStatus();

  /**
   *
   */

  Optional<Long> getRSeq();

  Optional<String> getServer();

  /**
   *
   * @param name
   * @param fields
   * @return
   */

  List<RValue> getAcceptResourcePriority();

  /**
   *
   * @param name
   * @param fields
   * @return
   */

  public <T> SipResponse withParsed(final String name, final List<T> fields);

  /**
   *
   */

  List<Authorization> getWWWAuthenticate();

  List<Authorization> getProxyAuthenticate();

  // -----

  SipResponse withPrepended(final RawHeader rawHeader);

  @Override
  SipResponse withPrepended(final String name, final Object field);

  @Override
  SipResponse withAppended(final String name, final Object field);

  @Override
  SipResponse withoutHeaders(final String... headerNames);

  @Override
  SipResponse withoutHeaders(final SipHeaderDefinition... headers);

  @Override
  SipResponse withFrom(final NameAddr na);

  @Override
  SipResponse withTo(final NameAddr na);

  /**
   * removes all existing contacts and adds this one
   *
   * @param na
   * @return
   */

  SipResponse withContact(final NameAddr na);

  SipResponse withContact(final Uri na);

  @Override
  SipResponse withCSeq(final long seqNum, final SipMethod method);

  @Override
  SipResponse withIncrementedCSeq(final SipMethod method);

  SipResponse withServer(final String string);

}
