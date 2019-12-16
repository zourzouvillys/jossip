package com.jive.sip.processor.rfc3261.message.impl;

import java.util.Optional;

public class BaseHeaderDefinition {

  private final String name;
  private final Optional<Character> sname;

  public BaseHeaderDefinition(final String name) {
    this(name, null);
  }

  public BaseHeaderDefinition(final String name, final Character sname) {
    this.name = name;
    this.sname = Optional.ofNullable(sname);
  }

  public String getName() {
    return this.name;
  }

  public Optional<Character> getShortName() {
    return this.sname;
  }

  public boolean matches(final String name) {

    if (this.name.equalsIgnoreCase(name)) {
      return true;
    }

    if (this.sname.isPresent()) {
      return this.sname.get().toString().equalsIgnoreCase(name);
    }

    return false;

  }

}
