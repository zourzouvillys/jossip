package com.jive.sip.parsers.core.terminal;

import java.nio.charset.StandardCharsets;

import com.google.common.primitives.Bytes;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;

/**
 * A parser which matches as many of the characters in the given constructor as possible.
 * 
 * @author theo
 * 
 */
public class CharactersParser implements Parser<CharSequence> {

  private final byte[] lookup;

  public CharactersParser(final byte[] lookup) {
    this.lookup = lookup;
  }

  public CharactersParser(final String lookup) {
    this(lookup.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public boolean find(final ParserContext context, final ValueListener<CharSequence> value) {

    if (context.remaining() == 0) {
      return false;
    }

    int matched = 0;

    for (int i = context.position(); i < context.limit(); ++i) {

      final byte ch = context.get(i);

      if (!Bytes.contains(this.lookup, ch)) {
        break;
      }

      matched++;

    }

    if (matched == 0) {
      return false;
    }

    if (value != null) {
      value.set(context.subSequence(context.position(), context.position() + matched));
    }

    context.position(context.position() + matched);

    return true;

  }

  @Override
  public String toString() {
    return new StringBuilder().append("str[").append(new String(this.lookup)).append("]").toString();
  }

}
