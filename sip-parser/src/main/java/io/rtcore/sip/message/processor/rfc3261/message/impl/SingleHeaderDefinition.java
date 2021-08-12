package io.rtcore.sip.message.processor.rfc3261.message.impl;

import java.util.Collection;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.message.api.SipHeaderDefinition;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ParserInput;
import io.rtcore.sip.message.parsers.core.ByteParserInput;
import io.rtcore.sip.message.parsers.core.DefaultParserContext;
import io.rtcore.sip.message.parsers.core.ParseFailureException;
import io.rtcore.sip.message.parsers.core.ValueHolder;

/**
 * A single value, single field header definition.
 * 
 * 
 * 
 * @param <T>
 */

public class SingleHeaderDefinition<T> extends BaseHeaderDefinition implements SipHeaderDefinition<T> {

  private final Parser<T> parser;

  public SingleHeaderDefinition(final Parser<T> parser, final String name, final Character sname) {
    super(name, sname);
    this.parser = parser;
  }

  public static <T> SipHeaderDefinition<T> create(final Parser<T> parser, final String name, final Character sname) {
    return new SingleHeaderDefinition<T>(parser, name, sname);
  }

  /**
   * Creates a SingleHeaderDefinition which accepts any string.
   * 
   * @param name
   * @param sname
   * @return
   */

  public static SipHeaderDefinition<String> create(final String name, final Character sname) {
    return new SingleHeaderDefinition<String>(null, name, sname);
  }

  public static SipHeaderDefinition<String> create(final String name) {
    return new SingleHeaderDefinition<String>(null, name, null);
  }

  public static <T> SipHeaderDefinition<T> create(final Parser<T> parser, final String name) {
    return new SingleHeaderDefinition<T>(parser, name, null);
  }

  @SuppressWarnings("unchecked")
  @Override
  public T parse(final Collection<RawHeader> headers) {

    for (final RawHeader header : headers) {

      if (matches(header.name())) {

        if (this.parser == null) {
          return (T) header.value();
        }

        final ParserInput input = ByteParserInput.fromString(header.value());
        final ParserContext ctx = new DefaultParserContext(input);
        final ValueHolder<T> holder = new ValueHolder<T>();

        if (!this.parser.find(ctx, holder)) {
          throw new ParseFailureException(String.format("Failed to parse '%s' header", header.name()));
        }

        if (ctx.remaining() > 0) {
          throw new ParseFailureException(String.format("Trailing garbage in '%s' header at pos %d", header.name(), ctx.position()));
        }

        return holder.value();

      }

    }

    // no value found.

    return null;

  }

}
