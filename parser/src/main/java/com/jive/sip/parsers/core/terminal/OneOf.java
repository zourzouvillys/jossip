package com.jive.sip.parsers.core.terminal;

import com.google.common.primitives.Bytes;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;

/**
 * 
 * @author theo
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
