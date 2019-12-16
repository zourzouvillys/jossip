package com.jive.sip.parameters.api;

import lombok.Value;

@Value
public class QuotedString {
  private String value;

  public static QuotedString from(String value) {
    return new QuotedString(value);
  }

}
