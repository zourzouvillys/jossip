/**
 * 
 */
package io.rtcore.sip.message.processor.uri.parsers;

import static io.rtcore.sip.message.parsers.core.ParserUtils.ALPHANUM_CHARS;
import static io.rtcore.sip.message.parsers.core.ParserUtils.ch;
import static io.rtcore.sip.message.parsers.core.ParserUtils.charSize;
import static io.rtcore.sip.message.parsers.core.ParserUtils.chars;
import static io.rtcore.sip.message.parsers.core.ParserUtils.str;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParserUtils;
import io.rtcore.sip.message.uri.UrnService;
import io.rtcore.sip.message.uri.UrnUri;

/**
 * 
 * 
 */
public class UrnUriParser implements UriSchemeParser<UrnUri> {

  private UrnUriParser(final String type) {
  }

  public static final UrnUriParser SERVICE = new UrnUriParser(UrnUri.SERVICE);

  private static final String LET_DIG_HYP = ALPHANUM_CHARS.concat("-");
  private static final Parser<CharSequence> TOP_LEVEL = charSize(LET_DIG_HYP, 1, 26);
  private static final Parser<CharSequence> SUB_SERVICE = chars(LET_DIG_HYP);
  private static final Parser<CharSequence> URN_TYPE = str("service:");
  private static final Parser<CharSequence> PERIOD = ch('.');

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.parsers.core.Parser#find(io.rtcore.sip.message.parsers.core.ParserContext,
   * io.rtcore.sip.message.parsers.core.ValueListener)
   */
  @Override
  public boolean find(final ParserContext ctx, final ValueListener<UrnUri> value) {
    final int pos = ctx.position();
    if (!ctx.skip(URN_TYPE)) {
      return false;
    }
    CharSequence service = ParserUtils.read(ctx, TOP_LEVEL);
    if (service == null) {
      ctx.position(pos);
      return false;
    }

    while (ctx.skip(PERIOD)) {
      final CharSequence subService = ParserUtils.read(ctx, SUB_SERVICE);
      if (subService == null) {
        ctx.position(pos);
        return false;
      }
      service = service.toString().concat("." + subService);
    }

    if (value != null) {
      value.set(new UrnUri(UrnUri.SERVICE, new UrnService(service.toString())));
    }

    return true;
  }
}
