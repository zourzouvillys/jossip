package com.jive.sip.parsers.core;

import java.nio.ByteBuffer;

import com.jive.sip.parsers.api.ParserInput;

public class ByteBufferParserInput implements ParserInput
{

  private final ByteBuffer buffer;

  public ByteBufferParserInput(final ByteBuffer buffer)
  {
    this.buffer = buffer;
  }

  @Override
  public byte get()
  {
    return this.buffer.get();
  }

  @Override
  public byte get(final int i)
  {
    return this.buffer.get(i);
  }

  @Override
  public ParserInput limit(final int newLimit)
  {
    this.buffer.limit(newLimit);
    return this;
  }

  @Override
  public int limit()
  {
    return this.buffer.limit();
  }

  @Override
  public ByteBufferParserInput mark()
  {
    this.buffer.mark();
    return this;
  }

  @Override
  public ByteBufferParserInput reset()
  {
    this.buffer.reset();
    return this;
  }

  @Override
  public SubParserInput slice()
  {
    return new SubParserInput(this, this.buffer.position(), this.buffer.remaining());
  }

  @Override
  public int position()
  {
    return this.buffer.position();
  }

  @Override
  public int remaining()
  {
    return this.buffer.remaining();
  }

  @Override
  public ParserInput position(final int newPosition)
  {
    this.buffer.position(newPosition);
    return this;
  }

  @Override
  public int length()
  {
    return this.limit();
  }

  @Override
  public char charAt(final int index)
  {
    return (char) this.get(index);
  }

  @Override
  public CharSequence subSequence(final int start, final int end)
  {
    return new SubParserInput(this, start, end);
  }

}
