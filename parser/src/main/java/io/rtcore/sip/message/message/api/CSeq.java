/**
 * 
 */
package io.rtcore.sip.message.message.api;

import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;

/**
 * 
 */
public final class CSeq {
  private final UnsignedInteger sequence;
  private final SipMethod method;

  public CSeq(long seq, SipMethod method) {
    this(UnsignedInteger.valueOf(seq), method);
  }

  public CSeq(UnsignedInteger seq, SipMethod method) {
    this.sequence = seq;
    this.method = method;
  }

  public long longValue() {
    return sequence.longValue();
  }

  public CSeq withNextSequence(SipMethod method) {
    return new CSeq(sequence.plus(UnsignedInteger.ONE), method);
  }

  public CSeq withNextSequence() {
    return withSequence(sequence().plus(UnsignedInteger.ONE));
  }

  public UnsignedInteger sequence() {
    return this.sequence;
  }

  public SipMethod method() {
    return this.method;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof CSeq))
      return false;
    final CSeq other = (CSeq) o;
    final Object this$sequence = this.sequence();
    final Object other$sequence = other.sequence();
    if (this$sequence == null ? other$sequence != null
                              : !this$sequence.equals(other$sequence))
      return false;
    final Object this$method = this.method();
    final Object other$method = other.method();
    if (this$method == null ? other$method != null
                            : !this$method.equals(other$method))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $sequence = this.sequence();
    result =
      (result * PRIME)
        + ($sequence == null ? 43
                             : $sequence.hashCode());
    final Object $method = this.method();
    result =
      (result * PRIME)
        + ($method == null ? 43
                           : $method.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "CSeq(" + this.sequence() + "," + this.method() + ")";
  }

  public CSeq withSequence(final UnsignedInteger sequence) {
    return this.sequence == sequence ? this
                                     : new CSeq(sequence, this.method);
  }

  public CSeq withMethod(final SipMethod method) {
    return this.method == method ? this
                                 : new CSeq(this.sequence, method);
  }

  public static CSeq of(String method) {
    return of(1, method);
  }

  public static CSeq of(long sequenceNumber, String method) {
    return new CSeq(sequenceNumber, SipMethod.of(method));
  }

  public String encode() {
    return RfcSerializerManager.defaultSerializer().writeValueAsString(this);
  }

  public static CSeq of(long sequenceNumber, SipMethod method) {
    return new CSeq(sequenceNumber, method);
  }

  public static CSeq of(SipMethod method) {
    return of(method.getMethod());
  }

}
