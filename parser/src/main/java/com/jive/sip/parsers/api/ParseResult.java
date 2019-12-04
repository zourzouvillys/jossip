package com.jive.sip.parsers.api;

/**
 * Holder for a parse operation.
 * 
 * @author theo
 * 
 * @param <T>
 */
public interface ParseResult<T>
{

  /**
   * @return The value this parser returned. May be null.
   */

  T value();

  /**
   * @return The length of the match. May be 0 even if there is a match.
   */

  Integer getMatchedLength();

  /**
   * @return true if it matched, otherwise false.
   */

  boolean matched();

  /**
   * @return the underlying CharSequence which matched.
   */

  CharSequence data();

}
