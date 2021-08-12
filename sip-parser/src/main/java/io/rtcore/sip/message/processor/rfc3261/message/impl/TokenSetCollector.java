package io.rtcore.sip.message.processor.rfc3261.message.impl;

import java.util.Set;

import com.google.common.collect.Sets;

import io.rtcore.sip.message.message.api.TokenSet;
import io.rtcore.sip.message.parsers.api.ValueCollector;

public class TokenSetCollector implements ValueCollector<CharSequence, TokenSet> {

  Set<CharSequence> set = Sets.newHashSet();

  @Override
  public void collect(final CharSequence token) {
    this.set.add(token);
  }

  @Override
  public TokenSet value() {
    return TokenSet.fromList(this.set);
  }

}
