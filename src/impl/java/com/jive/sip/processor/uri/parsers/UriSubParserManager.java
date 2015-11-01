/**
 * 
 */
package com.jive.sip.processor.uri.parsers;

import com.jive.sip.parsers.api.Parser;
import com.jive.sip.processor.uri.UriParserDefintion;
import com.jive.sip.uri.api.Uri;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public interface UriSubParserManager<T extends Uri>
{
  Parser<? extends T> getParser(String scheme);

  <E extends T> void register(UriParserDefintion<E> def);
}
