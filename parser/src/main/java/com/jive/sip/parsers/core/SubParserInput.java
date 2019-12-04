package com.jive.sip.parsers.core;

import com.jive.sip.parsers.api.ParserInput;

public class SubParserInput extends AbstractParserInput
{

  private final int start;
  private final ParserInput input;

  public SubParserInput(final ParserInput input, final int start, final int size)
  {
    super(size);
    this.start = start;
    this.input = input;
  }

  @Override
  public byte get(final int pos)
  {
    return this.input.get(this.start + pos);
  }

}
