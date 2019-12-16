package com.jive.sip.processor.rfc3261.message.impl;

import java.util.Collection;

import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.api.SipHeaderDefinition;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ParserInput;
import com.jive.sip.parsers.core.ByteParserInput;
import com.jive.sip.parsers.core.DefaultParserContext;
import com.jive.sip.parsers.core.ParseFailureException;
import com.jive.sip.parsers.core.ValueHolder;

/**
 * A single value, single field header definition.
 * 
 * @author theo
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
