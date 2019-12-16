package com.jive.sip.message.api.alertinfo;

public interface AlertInfoReference {

  <T> T apply(final AlertInfoReferenceVisitor<T> visitor);

}
