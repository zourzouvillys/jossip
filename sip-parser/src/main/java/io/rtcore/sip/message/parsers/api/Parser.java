package io.rtcore.sip.message.parsers.api;

/**
 * Base interface for all {@link Parser} implementations.
 * 
 * 
 * 
 * 
 * 
 * @param <T>
 */

public interface Parser<T> {

  /**
   * Implementations must attempt to match against the given input.
   * 
   * If the parse results in a match, then the context must be advanced one input element past the
   * match.
   * 
   * If the parse doesn't result in a match, then context must be at it's original place. You can
   * use mark() and reset() for this.
   * 
   * 
   * 
   * @param input
   *          The positioned input stream
   * 
   * @param value
   *          The value listener. It may be null if the caller doesn't care about the parsed value.
   * 
   * @return true if matched, else false.
   */

  boolean find(final ParserContext ctx, final ValueListener<T> value);

}
