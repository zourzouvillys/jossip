package com.jive.sip.parsers.api;

public interface ParserInput extends CharSequence
{

  /**
   * our current posaition.
   */

  int position();

  /**
   * Sets this buffer's position.
   */

  ParserInput position(final int newPosition);

  /**
   * how many bytes are left between our current position and the limit.
   */

  int remaining();

  /**
   * Relative get method. Reads the byte at this buffer's current position, and then increments the position.
   */

  byte get();

  /**
   * Absolute get method. Reads the byte at the given index.
   * 
   * @param index
   *          The index from which the byte will be read.
   * 
   * @return The byte at the given index
   * 
   */

  byte get(final int index);

  /**
   * Sets this buffer's limit. If the position is larger than the new limit then it is set to the new limit.
   * 
   * If the mark is defined and larger than the new limit then it is discarded.
   * 
   * @param newLimit
   *          - The new limit value; must be non-negative and no larger than this buffer's capacity
   * 
   * @return This buffer
   * 
   */

  ParserInput limit(final int newLimit);

  /**
   * Returns this buffer's limit.
   */

  int limit();

  /**
   * Sets this buffer's mark at its position.
   */

  ParserInput mark();

  /**
   * Resets this buffer's position to the previously-marked position.
   * 
   * Invoking this method neither changes nor discards the mark's value.
   * 
   */

  ParserInput reset();

  /**
   * Creates a new {@link ParserInput} who's content is a shared subsequence of this {@link ParserInput}.
   */

  ParserInput slice();

}
