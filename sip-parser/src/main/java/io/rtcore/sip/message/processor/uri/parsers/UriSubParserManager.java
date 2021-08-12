/**
 * 
 */
package io.rtcore.sip.message.processor.uri.parsers;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.processor.uri.UriParserDefintion;
import io.rtcore.sip.message.uri.Uri;

/**
 * 
 * 
 */
public interface UriSubParserManager<T extends Uri> {
  Parser<? extends T> getParser(String scheme);

  <E extends T> void register(UriParserDefintion<E> def);
}
