package com.jive.sip.processor.uri.parsers;

import static com.jive.sip.parsers.core.ParserUtils.ALPHANUM_CHARS;
import static com.jive.sip.parsers.core.ParserUtils.HEXDIG;
import static com.jive.sip.parsers.core.ParserUtils.and;
import static com.jive.sip.parsers.core.ParserUtils.chars;
import static com.jive.sip.parsers.core.ParserUtils.name;
import static com.jive.sip.parsers.core.ParserUtils.or;
import static com.jive.sip.parsers.core.ParserUtils.str;

import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParserUtils;
import com.jive.sip.uri.api.HttpUri;

public class HttpUriParser implements UriSchemeParser<HttpUri>
{

  public static final UriSchemeParser<HttpUri> HTTP = new HttpUriParser(false);
  public static final UriSchemeParser<HttpUri> HTTPS = new HttpUriParser(true);

  public static final String RESERVED_CHARS = ";/?:@&=+$,";
  public static final String MARK_CHARS = "-_.!~*'()";
  public static final String UNRESERVED_CHARS = ALPHANUM_CHARS.concat(MARK_CHARS);

  public static final Parser<CharSequence> URIC = name(or(
      chars(RESERVED_CHARS.concat(UNRESERVED_CHARS)),
      and(str("%"), HEXDIG, HEXDIG)
      ), "uric");
  private final boolean secure;

  public HttpUriParser(final boolean secure)
  {
    this.secure = secure;
  }

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<HttpUri> value)
  {
    // we are after 'http:' now.

    final int pos = ctx.position();

    final CharSequence number = ParserUtils.read(ctx, URIC);

    if (number == null)
    {
      ctx.position(pos);
      return false;
    }

    if (value != null)
    {
      value.set(new HttpUri(this.secure, number.toString()));
    }

    return true;

  }


}
