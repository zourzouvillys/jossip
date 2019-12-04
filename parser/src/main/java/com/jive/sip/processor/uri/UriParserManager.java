/**
 * 
 */
package com.jive.sip.processor.uri;

import java.util.Map;

import com.google.common.collect.Maps;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.processor.uri.parsers.UriSubParserManager;
import com.jive.sip.uri.api.Uri;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class UriParserManager implements UriSubParserManager<Uri>
{
  private final Map<String, Parser<? extends Uri>> registry = Maps.newHashMap();

  public <T extends Uri> void register(UriParserDefintion<T> definition)
  {
    registry.put(definition.getName(), definition.getParser());
  }

  public Parser<? extends Uri> getParser(Uri uri)
  {
    return getParser(uri.getScheme());
  }

  @Override
  public Parser<? extends Uri> getParser(String scheme)
  {
    Parser<? extends Uri> parser = registry.get(scheme);
    if (parser == null)
    {
      throw new RuntimeException("No parser for " + scheme);
    }
    return parser;
  }
}
