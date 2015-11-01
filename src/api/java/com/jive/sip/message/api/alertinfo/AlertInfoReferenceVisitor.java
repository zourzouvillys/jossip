package com.jive.sip.message.api.alertinfo;

public interface AlertInfoReferenceVisitor<T>
{

  T visit(final WellKnownAlertInfo i);

  T visit(final HttpUriAlertInfo i);

  T visit(final UnknownUriAlertInfo i);

}
