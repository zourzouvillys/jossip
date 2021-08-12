package io.rtcore.sip.message.parameters.impl;

import io.rtcore.sip.message.base.api.Token;

abstract class BaseParameterDefinition {
  protected final Token name;

  protected BaseParameterDefinition(CharSequence name) {
    this(Token.from(name));
  }

  protected BaseParameterDefinition(Token name) {
    this.name = name;
  }

  protected boolean matches(final Token name) {
    if (this.name.equals(name)) {
      return true;
    } else {
      return false;
    }
  }

  public Token name() {
    return this.name;
  }
}
