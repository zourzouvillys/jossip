package io.rtcore.sip.message.uri;

public interface UrnUriVisitor<T> extends UriVisitor<T> {

  public T visit(final UrnUri uri);

}
