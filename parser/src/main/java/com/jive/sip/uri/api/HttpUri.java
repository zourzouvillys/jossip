package com.jive.sip.uri.api;

import lombok.Value;

@Value
public class HttpUri implements Uri
{

  private boolean secure;
  private String opaque;

  @Override
  public String getScheme()
  {
    return secure ? "https" : "http";
  }

  public String toString()
  {
    return String.format("%s:%s", getScheme(), opaque);
  }

  @Override
  public <T> T apply(UriVisitor<T> visitor)
  {
    if (visitor instanceof HttpUriVisitor<?>)
    {
      return ((HttpUriVisitor<T>) visitor).visit(this);
    }
    return visitor.visit(this);
  }

  public static Uri secure(String hostAndPath)
  {
    return new HttpUri(true, String.format("//%s", hostAndPath));
  }

  public static Uri insecure(String hostAndPath)
  {
    return new HttpUri(false, String.format("//%s", hostAndPath));
  }

}
