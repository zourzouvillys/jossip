/**
 *
 */
package io.rtcore.sip.message.processor.uri;

import io.rtcore.sip.message.processor.uri.parsers.HttpUriParser;
import io.rtcore.sip.message.processor.uri.parsers.SipUriParser;
import io.rtcore.sip.message.processor.uri.parsers.TelUriParser;
import io.rtcore.sip.message.processor.uri.parsers.UrnUriParser;

/**
 * 
 *
 */

public class UriParserManagerBuilder {

  public static UriParserManager build() {
    final UriParserManager manager = new UriParserManager();
    registerParsers(manager);
    return manager;
  }

  private static void registerParsers(final UriParserManager manager) {
    manager.register(UriParserDefintion.build(TelUriParser.TEL, "tel"));
    manager.register(UriParserDefintion.build(SipUriParser.SIP, "sip"));
    manager.register(UriParserDefintion.build(SipUriParser.SIPS, "sips"));
    manager.register(UriParserDefintion.build(UrnUriParser.SERVICE, "urn"));
    manager.register(UriParserDefintion.build(HttpUriParser.HTTP, "http"));
    manager.register(UriParserDefintion.build(HttpUriParser.HTTPS, "https"));
  }
}
