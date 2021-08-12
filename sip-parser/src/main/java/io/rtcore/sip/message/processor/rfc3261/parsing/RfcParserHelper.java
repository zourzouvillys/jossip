/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.parsing;

import static io.rtcore.sip.message.parsers.core.ParserUtils.TOKEN;
import static io.rtcore.sip.message.parsers.core.ParserUtils.or;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.core.HostAndPortParser;
import io.rtcore.sip.message.parsers.core.QuotedStringParser;

/**
 * 
 *
 */
public class RfcParserHelper {
  public static final Parser<CharSequence> TOKEN_OR_QUOTED = or(TOKEN, QuotedStringParser.INSTANCE);
  public static final Parser<CharSequence> TOKEN_HOST_OR_QUOTED =
    or(TOKEN,
      HostAndPortParser.AS_CHAR_SEQUENCE,
      QuotedStringParser.INSTANCE);
}
