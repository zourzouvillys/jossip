/**
 * 
 */
package io.rtcore.sip.message.processor.uri;

import java.util.Map;

import com.google.common.collect.Maps;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.processor.uri.parsers.UriSubParserManager;
import io.rtcore.sip.message.uri.Uri;

/**
 * 
 * 
 */
public class UriParserManager implements UriSubParserManager<Uri> {

  private final Map<String, Parser<? extends Uri>> registry = Maps.newHashMap();

  public <T extends Uri> void register(UriParserDefintion<T> definition) {
    registry.put(definition.name(), definition.parser());
  }

  public Parser<? extends Uri> getParser(Uri uri) {
    return getParser(uri.getScheme());
  }

  @Override
  public Parser<? extends Uri> getParser(String scheme) {
    Parser<? extends Uri> parser = registry.get(scheme);
    if (parser == null) {
      throw new RuntimeException("No parser for " + scheme);
    }
    return parser;
  }
}
