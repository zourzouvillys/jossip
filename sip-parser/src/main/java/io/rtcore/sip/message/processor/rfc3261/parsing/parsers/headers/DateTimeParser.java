/**
 *
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static io.rtcore.sip.message.parsers.core.ParserUtils.ch;
import static io.rtcore.sip.message.parsers.core.ParserUtils.intp;
import static io.rtcore.sip.message.parsers.core.ParserUtils.or;
import static io.rtcore.sip.message.parsers.core.ParserUtils.str;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParseFailureException;

/**
 * 
 *
 */
public class DateTimeParser implements Parser<ZonedDateTime> {
  private static final Parser<CharSequence> WKDAY =
    or(str("Mon"),
      str("Tue"),
      str("Wed"),
      str("Thu"),
      str("Fri"),
      str("Sat"),
      str("Sun"));
  private static final Parser<CharSequence> COMMA = ch(',');
  private static final Parser<CharSequence> SP = ch(' ');
  private static final Parser<CharSequence> COLON = ch(':');
  private static final Parser<Integer> _2DIGIT = intp(2, 2);
  private static final Parser<Integer> _4DIGIT = intp(4, 4);

  private static final Parser<CharSequence> JAN = str("Jan");
  private static final Parser<CharSequence> FEB = str("Feb");
  private static final Parser<CharSequence> MAR = str("Mar");
  private static final Parser<CharSequence> APR = str("Apr");
  private static final Parser<CharSequence> MAY = str("May");
  private static final Parser<CharSequence> JUN = str("Jun");
  private static final Parser<CharSequence> JUL = str("Jul");
  private static final Parser<CharSequence> AUG = str("Aug");
  private static final Parser<CharSequence> SEP = str("Sep");
  private static final Parser<CharSequence> OCT = str("Oct");
  private static final Parser<CharSequence> NOV = str("Nov");
  private static final Parser<CharSequence> DEC = str("Dec");

  private static final Parser<CharSequence> GMT = str("GMT");

  private static final Parser<Integer> MONTH = new Parser<Integer>() {

    @Override
    public boolean find(final ParserContext ctx, final ValueListener<Integer> value) {
      Integer result = null;
      if (ctx.skip(JAN)) {
        result = 1;
      }
      else if (ctx.skip(FEB)) {
        result = 2;
      }
      else if (ctx.skip(MAR)) {
        result = 3;
      }
      else if (ctx.skip(APR)) {
        result = 4;
      }
      else if (ctx.skip(MAY)) {
        result = 5;
      }
      else if (ctx.skip(JUN)) {
        result = 6;
      }
      else if (ctx.skip(JUL)) {
        result = 7;
      }
      else if (ctx.skip(AUG)) {
        result = 8;
      }
      else if (ctx.skip(SEP)) {
        result = 9;
      }
      else if (ctx.skip(OCT)) {
        result = 10;
      }
      else if (ctx.skip(NOV)) {
        result = 11;
      }
      else if (ctx.skip(DEC)) {
        result = 12;
      }
      else {
        return false;
      }

      if (value != null) {
        value.set(result);
      }
      return true;
    }

    @Override
    public String toString() {
      return "month";
    }
  };

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.parsers.core.Parser#find(io.rtcore.sip.message.parsers.core.ParserContext,
   * io.rtcore.sip.message.parsers.core.ValueListener)
   */
  @Override
  public boolean find(final ParserContext ctx, final ValueListener<ZonedDateTime> value) {
    final int pos = ctx.position();
    try {
      ctx.read(WKDAY);
      ctx.read(COMMA);
      ctx.read(SP);

      final int day = ctx.read(_2DIGIT);
      ctx.read(SP);
      final int month = ctx.read(MONTH);
      ctx.read(SP);
      final int year = ctx.read(_4DIGIT);
      ctx.read(SP);
      final int hour = ctx.read(_2DIGIT);
      ctx.read(COLON);
      final int min = ctx.read(_2DIGIT);
      ctx.read(COLON);
      final int sec = ctx.read(_2DIGIT);
      ctx.read(SP);
      ctx.read(GMT);

      if (value != null) {
        value.set(ZonedDateTime.of(year, month, day, hour, min, sec, 0, ZoneId.of("UTC")));
      }

      return true;
    }
    catch (final ParseFailureException e) {
      ctx.position(pos);
      return false;
    }
  }
}
