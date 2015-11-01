package com.jive.sip.transport.udp;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Key used to reference a specific listener.
 * 
 * Used instead of passing a ref around.
 * 
 * @author theo
 */

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ListenerId
{

  @Getter
  private final int listenerId;

}
