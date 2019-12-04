/**
 * 
 */
package com.jive.sip.message.api;

import com.google.common.primitives.UnsignedInteger;

import lombok.Value;
import lombok.experimental.Wither;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 */

@Value
@Wither
public class CSeq
{

  private final UnsignedInteger sequence;
  private final SipMethod method;

  public CSeq(long seq, SipMethod method)
  {
    this(UnsignedInteger.valueOf(seq), method);
  }

  public CSeq(UnsignedInteger seq, SipMethod method)
  {
    this.sequence = seq;
    this.method = method;
  }

  public long longValue()
  {
    return sequence.longValue();
  }

  public CSeq withNextSequence(SipMethod method)
  {
    return new CSeq(sequence.plus(UnsignedInteger.ONE), method);
  }

}
