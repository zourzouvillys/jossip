package io.rtcore.sip.message.processor.rfc3261;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.message.api.SipHeaderDefinition;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ParserInput;
import io.rtcore.sip.message.parsers.api.ValueCollector;
import io.rtcore.sip.message.parsers.core.ByteParserInput;
import io.rtcore.sip.message.parsers.core.CollectionValueCollector;
import io.rtcore.sip.message.parsers.core.DefaultParserContext;
import io.rtcore.sip.message.parsers.core.ParserUtils;
import io.rtcore.sip.message.parsers.core.ValueHolder;
import io.rtcore.sip.message.processor.rfc3261.message.impl.BaseHeaderDefinition;
import io.rtcore.sip.message.processor.rfc3261.parsing.SipMessageParseFailureException;

/**
 * Header definition which parses each item as a set of comma seperated items.
 * 
 * 
 * 
 * @param <T>
 */

public class MultiHeaderDefinition<T, R> extends BaseHeaderDefinition implements SipHeaderDefinition<R> {

  private final Parser<T> parser;
  private final Supplier<? extends ValueCollector<T, R>> supplier;

  public MultiHeaderDefinition(
      final Parser<T> parser,
      final Supplier<? extends ValueCollector<T, R>> collector,
      final String name,
      final Character sname) {
    super(name, sname);
    this.parser = parser;
    this.supplier = collector;
  }

  public static <T, R> SipHeaderDefinition<R> create(
      final Parser<T> parser,
      final Supplier<? extends ValueCollector<T, R>> collector,
      final String name,
      final char sname) {
    return new MultiHeaderDefinition<T, R>(parser, collector, name, sname);
  }

  public static <T, R> SipHeaderDefinition<R> create(
      final Parser<T> parser,
      final Supplier<? extends ValueCollector<T, R>> collector,
      final String name) {
    return new MultiHeaderDefinition<T, R>(parser, collector, name, null);
  }

  public static <T> SipHeaderDefinition<List<T>> create(final Parser<T> parser, final String name, final char sname) {
    return new MultiHeaderDefinition<T, List<T>>(parser, collector(parser), name, sname);
  }

  public static <T> SipHeaderDefinition<List<T>> create(final Parser<T> parser, final String name) {
    return new MultiHeaderDefinition<T, List<T>>(parser, collector(parser), name, null);
  }

  public static <T> Supplier<? extends ValueCollector<T, List<T>>> collector(final Parser<T> parser) {
    return new Supplier<ValueCollector<T, List<T>>>() {
      @Override
      public ValueCollector<T, List<T>> get() {
        return new CollectionValueCollector<T>();
      }
    };
  }

  @Override
  public R parse(final Collection<RawHeader> headers) {

    ValueCollector<T, R> collector = null;

    for (final RawHeader header : headers) {

      if (matches(header.name())) {

        final ParserInput input = ByteParserInput.fromString(header.value());

        final ParserContext ctx = new DefaultParserContext(input);

        do {

          final ValueHolder<T> holder = ValueHolder.create();

          if (!this.parser.find(ctx, holder)) {
            // erp.
            throw new SipMessageParseFailureException("Failed to parse multiple header value");
          }

          if (collector == null) {
            collector = this.supplier.get();
          }

          collector.collect(holder.value());

        }
        while (ctx.skip(ParserUtils.COMMA));

      }

    }

    if (collector == null) {
      return null;
    }

    return collector.value();

  }

}
