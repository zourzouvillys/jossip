package com.jive.sip.processor.uri;

import java.nio.charset.StandardCharsets;

import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ParserInput;
import com.jive.sip.parsers.core.ByteParserInput;
import com.jive.sip.parsers.core.DefaultParserContext;
import com.jive.sip.uri.Uri;
import com.jive.sip.uri.UriVisitor;

import lombok.Value;

@Value
public class RawUri implements Uri {

  // TODO: this should be part of the context.
  private static final UriParserManager PARSER = UriParserManagerBuilder.build();

  private String scheme;
  private String opaque;

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
       * com.jive.sip.message.api.uri.UrnUri) that parses out or verfies some some
       * @see com.jive.sip.message.api.uri.UrnUri subclass the untimate gets passed to the visitor.
       */
      return listener.getUri().apply(visitor);
    }
    return visitor.visit(this);
  }
}
