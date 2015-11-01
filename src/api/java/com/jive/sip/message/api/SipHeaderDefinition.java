package com.jive.sip.message.api;

import java.util.Collection;
import java.util.Optional;

import com.jive.sip.base.api.RawHeader;

/**
 * Interfaced used to convert a SIP message header into a JVM object.
 * 
 * @author theo
 * 
 * @param <T>
 */

public interface SipHeaderDefinition<T>
{

  /**
   * Parses the given header definition from the message. If there were no matching headers found, then null should be
   * returned. If matches were found but didn't successfully match, then an exception should be thrown.
   * 
   * @param header
   * @return
   */

  T parse(final Collection<RawHeader> header);

  /**
   * @return The full header name.
   */

  String getName();

  /**
   * If this header has an optional short name, then return it.
   */

  Optional<Character> getShortName();

}
