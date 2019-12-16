package com.jive.sip.message.api.alertinfo;

import com.jive.sip.uri.api.Uri;

import lombok.Value;

@Value
public class UnknownUriAlertInfo implements AlertInfoReference {

  private Uri uri;

  @Override
  public <T> T apply(final AlertInfoReferenceVisitor<T> visitor) {
    return visitor.visit(this);
  }

}
