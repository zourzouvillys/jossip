package com.jive.sip.message.api;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.jive.sip.base.api.Token;

public class TokenSet implements Iterable<Token> {

  public static final TokenSet EMPTY = new TokenSet(Sets.<Token>newLinkedHashSet());

  private final Set<Token> tokens;

  protected TokenSet(final Set<Token> tokens) {
    this.tokens = tokens;
  }

  public static TokenSet fromTokens(final Collection<? extends Token> tokens) {
    final Set<Token> tts = Sets.newLinkedHashSet();
    for (final Token token : tokens) {
      tts.add(token);
    }
    return new TokenSet(tts);

  }

  public static TokenSet fromList(final Iterable<? extends CharSequence> tokens) {
    final Set<Token> tts = Sets.newLinkedHashSet();
    for (final CharSequence token : tokens) {
      tts.add(Token.from(token));
    }
    return new TokenSet(tts);
  }

  public boolean contains(final Token token) {
    return this.tokens.contains(token);
  }

  @Override
  public Iterator<Token> iterator() {
    return this.tokens.iterator();
  }

  public TokenSet with(final Token id) {
    if (this.tokens.contains(id)) {
      return this;
    }
    final Set<Token> toks = Sets.newHashSet(this.tokens);
    toks.add(id);
    return new TokenSet(toks);
  }

  public TokenSet without(final Token id) {
    if (!this.tokens.contains(id)) {
      return this;
    }
    final Set<Token> toks = Sets.newHashSet(this.tokens);
    toks.remove(id);
    return new TokenSet(toks);
  }

  /**
   * @return all tokens not also in the other list.
   */

  public TokenSet except(TokenSet other) {
    return TokenSet.fromTokens(tokens.stream()
      .filter(t -> !other.contains(t))
      .collect(Collectors.toList()));
  }

  /**
   * @return all tokens not also in the other list.
   */

  public TokenSet intersect(TokenSet other) {
    return TokenSet.fromTokens(tokens.stream()
      .filter(t -> other.contains(t))
      .collect(Collectors.toList()));
  }

  public boolean isEmpty() {
    return tokens.isEmpty();
  }

}
