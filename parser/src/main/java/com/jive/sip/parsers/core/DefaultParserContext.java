package com.jive.sip.parsers.core;

import java.util.Optional;

import com.google.common.base.Preconditions;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ParserInput;

public class DefaultParserContext implements ParserContext {

  private final ParserInput input;

  public DefaultParserContext(final ParserInput input) {
    this.input = input;
  }

  @Override
  public <T> boolean skip(final Parser<T> parser) {
    if (remaining() > 0) {
      return parser.find(this, null);
    }
    return false;
  }

  @Override
  public <T> T read(final Parser<T> parser) {

    if (remaining() == 0) {
      throw new ParseFailureException(String.format("Expected '%s', got EOF", parser.toString()));
    }

    final ValueHolder<T> value = ValueHolder.create();

    if (!parser.find(this, value)) {
      throw new ParseFailureException(
        String.format("Missing required '%s' at position %d - "
          +
          "got byte 0x%02x", parser.toString(), this.position(), this.peek() & 0xFF));
    }

    return value.value();

  }

  @Override
  public <T> Optional<T> tryRead(final Parser<T> parser) {

    final ValueHolder<T> value = ValueHolder.create();

    if (!parser.find(this, value)) {
      return Optional.empty();
    }

    return Optional.of(value.value());

  }

  @Override
  public <T> T read(final Parser<T> parser, final T defaultValue) {

    final ValueHolder<T> value = ValueHolder.create();

    if (!parser.find(this, value)) {
      return defaultValue;
    }

    return value.value();

  }

  @Override
  public int length() {
    return this.input.length();
  }

  @Override
  public char charAt(final int index) {
    Preconditions.checkPositionIndex(index, this.limit());
    return this.input.charAt(index);
  }

  @Override
  public CharSequence subSequence(final int start, final int end) {
    Preconditions.checkPositionIndexes(start, end, this.limit());
    return this.input.subSequence(start, end);
  }

  @Override
  public int position() {
    return this.input.position();
  }

  @Override
  public ParserInput position(final int newPosition) {
    return this.input.position(newPosition);
  }

  @Override
  public int remaining() {
    return this.input.remaining();
  }

  @Override
  public byte get() {
    return this.input.get();
  }

  @Override
  public byte get(final int index) {
    Preconditions.checkPositionIndex(index, this.limit());
    return this.input.get(index);
  }

  @Override
  public ParserInput limit(final int newLimit) {
    return this.input.limit(newLimit);
  }

  @Override
  public int limit() {
    return this.input.limit();
  }

  @Override
  public ParserInput mark() {
    return this.input.mark();
  }

  @Override
  public ParserInput reset() {
    return this.input.reset();
  }

  @Override
  public ParserInput slice() {
    return this.input.slice();
  }

  @Override
  public byte peek() {
    return this.input.get(this.position());
  }

}
