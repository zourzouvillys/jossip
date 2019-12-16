package com.jive.sip.parsers.api;

import java.util.Optional;

public interface ParserContext extends ParserInput {

  <T> boolean skip(final Parser<T> parser);

  /**
   * Attempts to parse the input with the given value, throwing an exception if it's unable to.
   * 
   * @param parser
   * @return
   */

  <T> T read(final Parser<T> parser);

  /**
   * attempt to read, don't fail if no chars left or if it fails to parse
   */

  <T> Optional<T> tryRead(Parser<T> parser);

  <T> T read(final Parser<T> parser, final T defaultValue);

  byte peek();

}
