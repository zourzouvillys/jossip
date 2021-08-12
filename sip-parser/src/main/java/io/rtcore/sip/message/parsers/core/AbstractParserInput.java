package io.rtcore.sip.message.parsers.core;

import com.google.common.base.Preconditions;

import io.rtcore.sip.message.parsers.api.ParserInput;

public abstract class AbstractParserInput implements ParserInput {

  private int size;
  private int mark = 0;
  private int position = 0;

  public AbstractParserInput(final int size) {
    this.size = size;
  }

  @Override
  public byte get() {
    return this.get(this.position++);
  }

  @Override
  public int limit() {
    return this.size;
  }

  @Override
  public ParserInput limit(final int newLimit) {
    this.size = newLimit;
    return this;
  }

  @Override
  public int remaining() {
    return this.size - this.position;
  }

  @Override
  public int position() {
    return this.position;
  }

  @Override
  public ParserInput mark() {
    this.mark = this.position;
    return this;
  }

  @Override
  public ParserInput reset() {
    this.position = this.mark;
    return this;
  }

  @Override
  public ParserInput slice() {
    return new SubParserInput(this, this.position(), this.remaining());
  }

  @Override
  public ParserInput position(final int newPosition) {
    this.position = newPosition;
    return this;
  }

  @Override
  public int length() {
    return this.limit();
  }

  @Override
  public char charAt(final int index) {
    return (char) this.get(index);
  }

  @Override
  public CharSequence subSequence(final int start, final int end) {
    Preconditions.checkPositionIndexes(start, end, this.length());
    return new SubParserInput(this, start, end - start).toString();
  }

  @Override
  public String toString() {
    final byte[] data = new byte[this.length()];
    for (int i = this.position(); i < this.limit(); ++i) {
      data[i - this.position()] = this.get(i);
    }
    return new String(data);

  }

}
