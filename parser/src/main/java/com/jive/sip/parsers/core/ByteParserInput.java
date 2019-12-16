package com.jive.sip.parsers.core;

import java.nio.charset.StandardCharsets;

import com.google.common.base.Preconditions;

public class ByteParserInput extends AbstractParserInput {

  private final byte[] input;
  private final int start;

  public ByteParserInput(final byte[] input, final int start, final int size) {
    super(size);
    this.start = start;
    this.input = input;
  }

  public ByteParserInput(final byte[] input) {
    this(input, 0, input.length);
  }

  public static ByteParserInput fromString(final String input) {
    return new ByteParserInput(input.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public byte get(final int pos) {
    Preconditions.checkPositionIndex(pos + this.start, this.input.length - 1);
    return this.input[this.start + pos];
  }

}
