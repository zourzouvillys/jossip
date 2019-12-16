package com.jive.sip.parsers.core;

import com.jive.sip.parsers.api.ValueListener;

public class StringValueListener implements ValueListener<CharSequence> {

  private String string;

  @Override
  public void set(final CharSequence val) {
    this.string = (String) val;
  }

  public String value() {
    return this.string;
  }

}
