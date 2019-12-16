/**
 * 
 */
package com.jive.sip.processor.uri;

import com.jive.sip.processor.uri.parsers.UriSchemeParser;
import com.jive.sip.uri.Uri;

import lombok.Value;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
@Value
public class UriParserDefintion<T extends Uri> {
  private final UriSchemeParser<? extends T> parser;
  private final String name;

  private UriParserDefintion(UriSchemeParser<? extends T> parser, String name) {
    this.parser = parser;
    this.name = name;
  }

  public static <E extends Uri> UriParserDefintion<E> build(UriSchemeParser<E> parser, String name) {
    return new UriParserDefintion<E>(parser, name);
  }
}
