package io.rtcore.sip.message.processor.uri;

import java.nio.charset.StandardCharsets;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ParserInput;
import io.rtcore.sip.message.parsers.core.ByteParserInput;
import io.rtcore.sip.message.parsers.core.DefaultParserContext;
import io.rtcore.sip.message.uri.Uri;
import io.rtcore.sip.message.uri.UriVisitor;

public final class RawUri implements Uri {
  // TODO: this should be part of the context.
  private static final UriParserManager PARSER = UriParserManagerBuilder.build();
  private final String scheme;
  private final String opaque;

  private RawUri(String scheme, String opaque) {
    this.scheme = scheme;
    this.opaque = opaque;
  }

  @Override
  public String toString() {
    return scheme + ":" + opaque;
  }

  @Override
  public <T> T apply(UriVisitor<T> visitor) {
    Parser<? extends Uri> parser = PARSER.getParser(this);
    return apply(visitor, parser);
  }

  private <T, E extends Uri> T apply(UriVisitor<T> visitor, Parser<E> parser) {
    ParserInput input = new ByteParserInput(this.getOpaque().getBytes(StandardCharsets.UTF_8));
    ParserContext ctx = new DefaultParserContext(input);
    UriListener<E> listener = new UriListener<E>();
    if (parser.find(ctx, listener)) {
      /*
       * This calls the apply method of the parsed URI rather then simple passing the parsed URI to
       * the visitor so that other URI objects can do other sub-parsing or processing in their own
       * apply methods. One example could be parsing out a URN URI as defined in RFC 5031 (@see
       * io.rtcore.sip.message.message.api.uri.UrnUri) that parses out or verfies some some
       * @see io.rtcore.sip.message.message.api.uri.UrnUri subclass the untimate gets passed to the visitor.
       */
      return listener.uri().apply(visitor);
    }
    return visitor.visit(this);
  }

  public String getOpaque() {
    return this.opaque;
  }

  @Override
  public String getScheme() {
    return this.scheme;
  }

  public static Uri of(String scheme, String opaque) {
    return new RawUri(scheme, opaque);
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof RawUri)) return false;
    final RawUri other = (RawUri) o;
    final Object this$scheme = this.scheme;
    final Object other$scheme = other.scheme;
    if (this$scheme == null ? other$scheme != null : !this$scheme.equals(other$scheme)) return false;
    final Object this$opaque = this.opaque;
    final Object other$opaque = other.opaque;
    if (this$opaque == null ? other$opaque != null : !this$opaque.equals(other$opaque)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $scheme = this.scheme;
    result = result * PRIME + ($scheme == null ? 43 : $scheme.hashCode());
    final Object $opaque = this.opaque;
    result = result * PRIME + ($opaque == null ? 43 : $opaque.hashCode());
    return result;
  }
}
