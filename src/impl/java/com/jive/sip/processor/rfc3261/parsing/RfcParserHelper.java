/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing;

import static com.jive.sip.parsers.core.ParserUtils.TOKEN;
import static com.jive.sip.parsers.core.ParserUtils.or;

import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.core.HostAndPortParser;
import com.jive.sip.parsers.core.QuotedStringParser;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */
public class RfcParserHelper
{
  public static final Parser<CharSequence> TOKEN_OR_QUOTED = or(TOKEN, QuotedStringParser.INSTANCE);
  public static final Parser<CharSequence> TOKEN_HOST_OR_QUOTED = or(TOKEN, 
      HostAndPortParser.AS_CHAR_SEQUENCE, QuotedStringParser.INSTANCE);
}
