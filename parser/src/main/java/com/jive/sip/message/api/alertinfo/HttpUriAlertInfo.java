package com.jive.sip.message.api.alertinfo;

import lombok.Value;

@Value
public class HttpUriAlertInfo implements AlertInfoReference {
  
  private String uri;

  @Override
  public <T> T apply(final AlertInfoReferenceVisitor<T> visitor) {
    return visitor.visit(this);
  }

}
