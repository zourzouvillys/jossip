package com.jive.sip.base.api;

import lombok.Value;

/**
 * Representation of a single header in a SIP message.
 * 
 * 
 * @author theo
 */
@Value
public final class RawHeader {

  // * NOTE: this header is expected to be immutable. do NOT add any methods which mutate the value.
  private final String name;
  private final String value;

}
