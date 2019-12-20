/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import java.util.Collection;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.message.api.headers.ParameterizedUri;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.tools.ParameterBuilder;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParameterParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.SipMessageParseFailureException;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.uri.UriParser;
import io.rtcore.sip.message.uri.Uri;

/**
 * 
 * 
 */
public class ParameterizedUriParser implements Parser<ParameterizedUri> {
  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.parsers.core.Parser#find(io.rtcore.sip.message.parsers.core.ParserContext,
   * io.rtcore.sip.message.parsers.core.ValueListener)
   */
  @Override
  public boolean find(final ParserContext ctx, final ValueListener<ParameterizedUri> value) {
    final int pos = ctx.position();

    try {
      final Uri uri = ctx.read(UriParser.URI_WITHBRACKETS);

      final Collection<RawParameter> params;

      if (ctx.remaining() > 0) {
        params = ctx.read(ParameterParser.getInstance());
      }
      else {
        params = Lists.newLinkedList();
      }

      if (value != null) {
        value.set(new ParameterizedUri(uri, ParameterBuilder.from(params)));
      }
      return true;
    }
    catch (final SipMessageParseFailureException e) {
      ctx.position(pos);
      return false;
    }
  }
}
