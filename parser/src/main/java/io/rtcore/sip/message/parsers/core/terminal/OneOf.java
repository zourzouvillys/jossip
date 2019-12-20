package io.rtcore.sip.message.parsers.core.terminal;

import com.google.common.primitives.Bytes;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;

/**
 * 
 * 
 * 
 */

public class OneOf implements Parser<CharSequence> {

  private final byte[] bytes;

  public OneOf(final byte[] bytes) {
    this.bytes = bytes;
  }

  @Override
  public boolean find(final ParserContext context, final ValueListener<CharSequence> value) {

    final int pos = context.position();

    if (context.remaining() > 0 && Bytes.contains(this.bytes, context.get())) {
      return true;
    }

    context.position(pos);

    return false;

  }

  @Override
  public String toString() {
    return new StringBuilder().append("oneOf(").append(this.bytes).append(")").toString();
  }

}
