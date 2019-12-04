package com.jive.sip.processor.rfc3261.parsing.parsers;

import static com.jive.sip.parsers.core.ParserUtils.SLASH;
import static com.jive.sip.parsers.core.ParserUtils.TOKEN;

import com.jive.sip.message.api.ViaProtocol;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.processor.rfc3261.parsing.SipMessageParseFailureException;

public class ViaProtocolParser implements Parser<ViaProtocol>
{

  @Override
  public boolean find(final ParserContext context, final ValueListener<ViaProtocol> proto)
  {

    final int pos = context.position();

    try
    {

      final CharSequence name = context.read(TOKEN);
      context.read(SLASH);
      final CharSequence version = context.read(TOKEN);
      context.read(SLASH);
      final CharSequence transport = context.read(TOKEN);

      if (proto != null)
      {
        proto.set(new ViaProtocol(name, version, transport));
      }

      return true;

    }
    catch (final SipMessageParseFailureException e)
    {
      context.position(pos);
      return false;
    }

  }

  @Override
  public String toString()
  {
    return "via-parm";
  }

}
