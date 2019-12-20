package io.rtcore.sip.message.processor.rfc3261.parsing.parsers;

import static io.rtcore.sip.message.parsers.core.ParserUtils.SLASH;
import static io.rtcore.sip.message.parsers.core.ParserUtils.TOKEN;

import io.rtcore.sip.message.message.api.ViaProtocol;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.processor.rfc3261.parsing.SipMessageParseFailureException;

public class ViaProtocolParser implements Parser<ViaProtocol> {

  @Override
  public boolean find(final ParserContext context, final ValueListener<ViaProtocol> proto) {

    final int pos = context.position();

    try {

      final CharSequence name = context.read(TOKEN);
      context.read(SLASH);
      final CharSequence version = context.read(TOKEN);
      context.read(SLASH);
      final CharSequence transport = context.read(TOKEN);

      if (proto != null) {
        proto.set(new ViaProtocol(name, version, transport));
      }

      return true;

    }
    catch (final SipMessageParseFailureException e) {
      context.position(pos);
      return false;
    }

  }

  @Override
  public String toString() {
    return "via-parm";
  }

}
