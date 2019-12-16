package com.jive.sip.uri;

public interface HttpUriVisitor<T> extends UriVisitor<T> {
  public T visit(final HttpUri uri);
}
