package com.jive.sip.message.api.alertinfo;

import com.jive.sip.uri.api.UrnUri;

import lombok.Value;

@Value
public class WellKnownAlertInfo implements AlertInfoReference
{

  private final UrnUri uri;

  public WellKnownAlertInfo(final UrnUri uri)
  {
    this.uri = uri;
  }

  @Override
  public <T> T apply(final AlertInfoReferenceVisitor<T> visitor)
  {
    return visitor.visit(this);
  }

}
