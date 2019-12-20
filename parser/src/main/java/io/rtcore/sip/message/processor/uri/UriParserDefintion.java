/**
 * 
 */
package io.rtcore.sip.message.processor.uri;

import io.rtcore.sip.message.processor.uri.parsers.UriSchemeParser;
import io.rtcore.sip.message.uri.Uri;

/**
 * 
 */
public final class UriParserDefintion<T extends Uri> {
  private final UriSchemeParser<? extends T> parser;
  private final String name;

  private UriParserDefintion(UriSchemeParser<? extends T> parser, String name) {
    this.parser = parser;
    this.name = name;
  }

  public static <E extends Uri> UriParserDefintion<E> build(UriSchemeParser<E> parser, String name) {
    return new UriParserDefintion<E>(parser, name);
  }

  public UriSchemeParser<? extends T> parser() {
    return this.parser;
  }

  public String name() {
    return this.name;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof UriParserDefintion)) return false;
    final UriParserDefintion<?> other = (UriParserDefintion<?>) o;
    final Object this$parser = this.parser();
    final Object other$parser = other.parser();
    if (this$parser == null ? other$parser != null : !this$parser.equals(other$parser)) return false;
    final Object this$name = this.name();
    final Object other$name = other.name();
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $parser = this.parser();
    result = result * PRIME + ($parser == null ? 43 : $parser.hashCode());
    final Object $name = this.name();
    result = result * PRIME + ($name == null ? 43 : $name.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "UriParserDefintion(parser=" + this.parser() + ", name=" + this.name() + ")";
  }
}
