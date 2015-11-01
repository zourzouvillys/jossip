package com.jive.sip.message.api.alertinfo;

import com.jive.sip.uri.api.HttpUri;
import com.jive.sip.uri.api.HttpUriVisitor;
import com.jive.sip.uri.api.Uri;
import com.jive.sip.uri.api.UrnUri;
import com.jive.sip.uri.api.UrnUriVisitor;

public class AlertInfoUriExtractor implements HttpUriVisitor<AlertInfoReference>, UrnUriVisitor<AlertInfoReference>
{

  private static final AlertInfoUriExtractor INSTANCE = new AlertInfoUriExtractor();

  @Override
  public AlertInfoReference visit(final Uri unknown)
  {
    return new UnknownUriAlertInfo(unknown);
  }

  @Override
  public AlertInfoReference visit(final HttpUri uri)
  {
    return new HttpUriAlertInfo(uri.toString());
  }

  @Override
  public AlertInfoReference visit(final UrnUri uri)
  {
    return new WellKnownAlertInfo(uri);
  }

  public static AlertInfoUriExtractor getInstance()
  {
    return INSTANCE;
  }

}
