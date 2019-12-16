/**
 *
 */
package com.jive.sip.parsers.core;

import java.util.Stack;

import com.google.common.net.HostAndPort;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;

/**
 * This parser matches a domain name, IPv4 address, IPv6 reference, and host:port combinations. The
 * primary parser is HostAndPort.
 *
 * @author Jeff Hutchins <jhutchins@getjive.com>
 */

public class HostAndPortParser implements Parser<HostAndPort> {
  private HostAndPortParser() {

  }

  public static final HostAndPortParser INSTANCE = new HostAndPortParser();
  public static final Parser<CharSequence> AS_CHAR_SEQUENCE = new Parser<CharSequence>() {

    @Override
    public boolean find(final ParserContext ctx, final ValueListener<CharSequence> value) {
      final int pos = ctx.position();

      if (!ctx.skip(INSTANCE)) {
        return false;
      }

      ParserHelper.notifyValue(ctx, value, pos);
      return true;
    }

    @Override
    public String toString() {
      return "host";
    }
  };
  private static final Parser<CharSequence> ALPHANUM_DASH =
    ParserUtils.name(
      ParserUtils.chars(ParserUtils.ALPHANUM_CHARS.concat("-")),
      "[a-z0-9-]+");

  public static class DomainComponentParser implements Parser<CharSequence> {

    private final Parser<CharSequence> initial;

    public DomainComponentParser(final Parser<CharSequence> initial) {
      this.initial = initial;
    }

    @Override
    public boolean find(final ParserContext ctx, final ValueListener<CharSequence> value) {

      final int pos = ctx.position();

      if (!ctx.skip(this.initial)) {
        ctx.position(pos);
        return false;
      }

      if (!ctx.skip(ALPHANUM_DASH)) {
        return ParserHelper.notifyValue(ctx, value, pos);
      }

      // rewind over a given char.
      ParserHelper.rewindOver(ctx, '-');

      return ParserHelper.notifyValue(ctx, value, pos);

    }

    @Override
    public String toString() {
      return "domain-component";
    }

  };

  public static final Parser<CharSequence> DOMAIN_LABEL =
    ParserUtils.name(
      new DomainComponentParser(ParserUtils.ALPHANUM),
      "domainlabel");
  public static final Parser<CharSequence> TOP_LABEL =
    ParserUtils.name(new DomainComponentParser(
      ParserUtils.ALPHA), "toplabel");

  private static final Parser<CharSequence> DOT = ParserUtils.ch('.');

  private static final Parser<CharSequence> DOMAIN_AND_DOT = ParserUtils.and(DOMAIN_LABEL, DOT);

  /**
   * This parser handles a hostname matching RFC 3261 rules. It must not match an IPv4 or IPv6
   * address.
   *
   * hostname = *( domainlabel "." ) toplabel [ "." ]
   *
   *
   */

  public static final Parser<CharSequence> HOSTNAME = new Parser<CharSequence>() {

    @Override
    public boolean find(final ParserContext ctx, final ValueListener<CharSequence> value) {

      final int pos = ctx.position();
      final Stack<Integer> dots = new Stack<Integer>();
      dots.push(pos);

      while (ctx.skip(DOMAIN_AND_DOT)) {
        dots.push(ctx.position());
      }

      while (!ctx.skip(TOP_LABEL) && !dots.isEmpty()) {
        ctx.position(dots.pop());
      }

      if (ctx.position() == pos) {
        return false;
      }

      ctx.skip(DOT);

      ParserHelper.notifyValue(ctx, value, pos);
      return true;

    }

    @Override
    public String toString() {
      return "hostname";
    }

  };

  public static final Parser<CharSequence> IPV4_ADDRESS = new Parser<CharSequence>() {

    @Override
    public boolean find(final ParserContext ctx, final ValueListener<CharSequence> value) {

      final int pos = ctx.position();

      for (int j = 0; j < 4; j++) {

        final Integer part = ctx.read(ParserUtils._1_3DIGIT, null);

        if ((part == null) || (part.intValue() < 0) || (part.intValue() > 255)) {
          ctx.position(pos);
          return false;
        }

        if (j < 3) {
          if (!ctx.skip(DOT)) {
            ctx.position(pos);
            return false;
          }
        }
      }

      return ParserHelper.notifyValue(ctx, value, pos);

    }

    @Override
    public String toString() {
      return "IPV4_ADDRESS";
    }

  };

  public static final Parser<CharSequence> HEX4 =
    ParserUtils.name(ParserUtils.charSize(ParserUtils.HEXDIGIT_CHARS, 1, 4), "HEX4");

  public static final Parser<CharSequence> HEXSEQ = new Parser<CharSequence>() {

    @Override
    public boolean find(final ParserContext ctx, final ValueListener<CharSequence> value) {
      final int pos = ctx.position();

      if (!ctx.skip(HEX4)) {
        return false;
      }

      while (true) {
        if (!ctx.skip(ParserUtils.and(ParserUtils.ch(':'),
          ParserUtils.and(ParserUtils.not(IPV4_ADDRESS), HEX4)))) {
          break;
        }
      }

      ParserHelper.notifyValue(ctx, value, pos);
      return true;
    }

    @Override
    public String toString() {
      return "HEXSEQ";
    }
  };

  public static final Parser<CharSequence> HEXPART = new Parser<CharSequence>() {

    @Override
    public boolean find(final ParserContext ctx, final ValueListener<CharSequence> value) {
      final int pos = ctx.position();

      final boolean found = ctx.skip(HEXSEQ);

      if (!ctx.skip(ParserUtils.str("::"))) {
        if (!found) {
          return false;
        }

        ParserHelper.notifyValue(ctx, value, pos);
        return true;
      }

      ctx.skip(HEXSEQ);

      ParserHelper.notifyValue(ctx, value, pos);
      return true;
    }

    @Override
    public String toString() {
      return "HEXPART";
    }
  };

  public static final Parser<CharSequence> IPV6_REFFERENCE = new Parser<CharSequence>() {

    @Override
    public boolean find(final ParserContext ctx, final ValueListener<CharSequence> value) {
      final int pos = ctx.position();

      if (!ParserUtils.ch('[').find(ctx, value)) {
        return false;
      }

      if (!HEXPART.find(ctx, value)) {
        ctx.position(pos);
        return false;
      }

      ParserUtils.and(ParserUtils.ch(':'), IPV4_ADDRESS).find(ctx, value);

      if (!ParserUtils.ch(']').find(ctx, value)) {
        ctx.position(pos);
        return false;
      }

      ParserHelper.notifyValue(ctx, value, pos);
      return true;
    }

    @Override
    public String toString() {
      return "IPV6_REFFERENCE";
    }

  };

  public static final Parser<CharSequence> HOST =
    ParserUtils.name(
      ParserUtils.or(IPV6_REFFERENCE, IPV4_ADDRESS, HOSTNAME),
      "host");

  /**
   * Parse the host and an optional port. Host could be a proper DNS hostname, and IPv4 address or a
   * IPv6 reference.
   */

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<HostAndPort> listener) {

    final int pos = ctx.position();

    try {

      final String host = ctx.read(HOST).toString();

      if (ctx.skip(ParserUtils.COLON)) {

        final int port = ctx.read(ParserUtils.uint(1, 5)).intValue();

        if ((port < 0) || (port > 65535)) {
          // TODO: TPZ: should this be an error rather than no match?
          ctx.position(pos);
          return false;
        }

        if (listener != null) {
          listener.set(HostAndPort.fromParts(host, port));
        }

      }
      else {

        if (listener != null) {
          listener.set(HostAndPort.fromString(host));
        }

      }

      return true;

    }
    catch (final ParseFailureException e) {
      ctx.position(pos);
      return false;
    }

  }

  @Override
  public String toString() {
    return "host[:port]";
  }

}
