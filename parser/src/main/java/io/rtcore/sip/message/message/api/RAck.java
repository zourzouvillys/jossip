package io.rtcore.sip.message.message.api;

import com.google.common.primitives.UnsignedInteger;

public final class RAck {
  private final UnsignedInteger reliableSequence;
  private final CSeq sequence;

  public RAck(final UnsignedInteger reliableSequence, final CSeq sequence) {
    this.reliableSequence = reliableSequence;
    this.sequence = sequence;
  }

  public UnsignedInteger reliableSequence() {
    return this.reliableSequence;
  }

  public CSeq sequence() {
    return this.sequence;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof RAck)) return false;
    final RAck other = (RAck) o;
    final Object this$reliableSequence = this.reliableSequence();
    final Object other$reliableSequence = other.reliableSequence();
    if (this$reliableSequence == null ? other$reliableSequence != null : !this$reliableSequence.equals(other$reliableSequence)) return false;
    final Object this$sequence = this.sequence();
    final Object other$sequence = other.sequence();
    if (this$sequence == null ? other$sequence != null : !this$sequence.equals(other$sequence)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $reliableSequence = this.reliableSequence();
    result = (result * PRIME) + ($reliableSequence == null ? 43 : $reliableSequence.hashCode());
    final Object $sequence = this.sequence();
    result = (result * PRIME) + ($sequence == null ? 43 : $sequence.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "RAck(reliableSequence=" + this.reliableSequence() + ", sequence=" + this.sequence() + ")";
  }

  public static RAck of(long rseq, CSeq cseq) {
    return new RAck(UnsignedInteger.valueOf(rseq), cseq);
  }
}
