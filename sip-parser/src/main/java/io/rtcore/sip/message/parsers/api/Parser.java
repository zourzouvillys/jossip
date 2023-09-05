package io.rtcore.sip.message.parsers.api;

import io.rtcore.sip.message.parsers.core.ByteParserInput;
import io.rtcore.sip.message.parsers.core.DefaultParserContext;

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
   * @param ctx
   *          The positioned input stream
   *
   * @param value
   *          The value listener. It may be null if the caller doesn't care about the parsed value.
   *
   * @return true if matched, else false.
   */

  boolean find(final ParserContext ctx, final ValueListener<T> value);

  /**
   *
   * @param na
   * @return
   */

  default T parseValue(final String na) {
    final ParserInput input = ByteParserInput.fromString(na);
    final ParserContext context = new DefaultParserContext(input);
    try {
      return context.read(this);
    }
    finally {
      if (input.remaining() > 0) {
        throw new RuntimeException("trailing data");
      }
    }
  }

  /**
   * parse the leading value, not failing if there is trailing data.
   */

  default T parseFirstValue(final String na) {
    final ParserInput input = ByteParserInput.fromString(na);
    final ParserContext context = new DefaultParserContext(input);
    return context.read(this);
  }

}
