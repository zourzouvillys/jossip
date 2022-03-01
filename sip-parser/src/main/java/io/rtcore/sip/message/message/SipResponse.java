package io.rtcore.sip.message.message;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import io.rtcore.sip.common.iana.SipStatusCodes;
import io.rtcore.sip.message.auth.headers.Authorization;
import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.message.api.SipHeaderDefinition;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.message.api.headers.RValue;
import io.rtcore.sip.message.uri.Uri;

/**
 * Representation of a SIP response.
 *
 *
 *
 */
public interface SipResponse extends SipMessage {

  SipResponseStatus getStatus();

  /**
   *
   */

  OptionalLong getRSeq();

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

  <T> SipResponse withParsed(final String name, final List<T> fields);

  /**
   *
   */

  List<Authorization> getWWWAuthenticate();

  List<Authorization> getProxyAuthenticate();

  @Override
  <T> SipResponse withReplacedHeader(final SipHeaderDefinition<T> header, final T value);

  // -----

  SipResponse withPrepended(final RawHeader rawHeader);

  @Override
  SipResponse withPrepended(final String name, final Object field);

  @Override
  SipResponse withAppended(final String name, final Object field);

  @Override
  SipResponse withoutHeaders(final String... headerNames);

  @Override
  SipResponse withoutHeaders(final SipHeaderDefinition<?>... headers);

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
