package io.rtcore.sip.message.message.api.alertinfo;

import io.rtcore.sip.message.uri.HttpUri;
import io.rtcore.sip.message.uri.HttpUriVisitor;
import io.rtcore.sip.message.uri.Uri;
import io.rtcore.sip.message.uri.UrnUri;
import io.rtcore.sip.message.uri.UrnUriVisitor;

public class AlertInfoUriExtractor implements HttpUriVisitor<AlertInfoReference>, UrnUriVisitor<AlertInfoReference> {

  private static final AlertInfoUriExtractor INSTANCE = new AlertInfoUriExtractor();

  @Override
  public AlertInfoReference visit(final Uri unknown) {
    return new UnknownUriAlertInfo(unknown);
  }

  @Override
  public AlertInfoReference visit(final HttpUri uri) {
    return new HttpUriAlertInfo(uri.toString());
  }

  @Override
  public AlertInfoReference visit(final UrnUri uri) {
    return new WellKnownAlertInfo(uri);
  }

  public static AlertInfoUriExtractor getInstance() {
    return INSTANCE;
  }

}
