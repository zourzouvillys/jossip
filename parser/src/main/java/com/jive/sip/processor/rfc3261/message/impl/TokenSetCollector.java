package com.jive.sip.processor.rfc3261.message.impl;

import java.util.Set;

import com.google.common.collect.Sets;
import com.jive.sip.message.api.TokenSet;
import com.jive.sip.parsers.api.ValueCollector;

public class TokenSetCollector implements ValueCollector<CharSequence, TokenSet>
{

  Set<CharSequence> set = Sets.newHashSet();

  @Override
  public void collect(final CharSequence token)
  {
    this.set.add(token);
  }

  @Override
  public TokenSet value()
  {
    return TokenSet.fromList(this.set);
  }

}
