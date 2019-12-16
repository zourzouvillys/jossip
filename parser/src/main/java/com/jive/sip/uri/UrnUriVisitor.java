package com.jive.sip.uri;

public interface UrnUriVisitor<T> extends UriVisitor<T> {

  public T visit(final UrnUri uri);

}
