package com.jive.sip.parameters.impl;

import com.jive.sip.base.api.Token;

import lombok.Getter;

abstract class BaseParameterDefinition {

  @Getter
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
    }
    else {
      return false;
    }
  }

}
