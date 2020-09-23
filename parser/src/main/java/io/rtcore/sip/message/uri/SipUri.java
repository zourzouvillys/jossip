package io.rtcore.sip.message.uri;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.net.HostAndPort;
import com.google.common.net.UrlEscapers;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.message.api.SipTransport;
import io.rtcore.sip.message.parameters.api.BaseParameterizedObject;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.SipParameterDefinition;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parameters.impl.TokenParameterDefinition;
import io.rtcore.sip.message.processor.uri.parsers.SipUriParser;

/**
 * A 'sip' or 'sips' URI.
 *
 * Note that ALL of these fields are immutable! Don't modify the current object! Instead, return a
 * new instance with the reflected changes.
 *
 */

public class SipUri extends BaseParameterizedObject<SipUri> implements Uri {

  public static final String SIP = "sip";
  public static final String SIPS = "sips";

  public static final SipUri ANONYMOUS = SipUri.fromUserAndHost("anonymous", "anonymous.invalid");

  private static final TokenParameterDefinition P_USER = new TokenParameterDefinition(Token.from("user"));

  private static final TokenParameterDefinition PTransport = new TokenParameterDefinition(Token.from("transport"));

  public static final SipParameterDefinition<Token> PMethod = new TokenParameterDefinition("method");

  private final String scheme;
  private final Optional<UserInfo> userinfo;
  private final HostAndPort host;
  private final Collection<RawHeader> headers;

  public SipUri(final HostAndPort host) {
    this(SIP, Optional.empty(), host, DefaultParameters.EMPTY, null);
  }

  public SipUri(final UserInfo userinfo, final HostAndPort host) {
    this(SIP, Optional.ofNullable(userinfo), host, DefaultParameters.EMPTY, null);
  }

  public SipUri(final UserInfo userinfo, final String hostAndPoort) {
    this(SIP, Optional.ofNullable(userinfo), HostAndPort.fromString(hostAndPoort), DefaultParameters.EMPTY, null);
  }

  public SipUri(final String scheme, final UserInfo userinfo, final HostAndPort host) {
    this(scheme, Optional.ofNullable(userinfo), host, DefaultParameters.EMPTY, null);
  }

  public SipUri(
      final boolean secure,
      final UserInfo userinfo,
      final HostAndPort host,
      final Parameters parameters) {
    this(
      secure ? SIPS
             : SIP,
      Optional.ofNullable(userinfo),
      host,
      parameters,
      null);
  }

  public SipUri(
      final String scheme,
      final Optional<UserInfo> userinfo,
      final HostAndPort host,
      final Collection<RawHeader> headers) {
    this(scheme, userinfo, host, DefaultParameters.EMPTY, headers);
  }

  public SipUri(
      final String scheme,
      final UserInfo userinfo,
      final HostAndPort host,
      final Parameters parameters,
      final Collection<RawHeader> headers) {
    this(scheme, Optional.ofNullable(userinfo), host, parameters, headers);
  }

  public SipUri(
      final String scheme,
      final UserInfo userinfo,
      final HostAndPort host,
      final Parameters parameters) {
    this(scheme, Optional.ofNullable(userinfo), host, parameters, null);
  }

  public SipUri(
      final String scheme,
      final Optional<UserInfo> userinfo,
      final HostAndPort host,
      final Parameters parameters) {
    this(scheme, userinfo, host, parameters, null);
  }

  public SipUri(
      final String scheme,
      final Optional<UserInfo> userinfo,
      final HostAndPort host,
      final Parameters parameters,
      final Collection<RawHeader> headers) {
    this.scheme = scheme;
    this.userinfo = userinfo;
    this.host = host;
    this.parameters = parameters;
    this.headers =
      headers == null ? new LinkedHashSet<>()
                      : headers;
  }

  /**
   * @return true if this is a sips uri, otherwise false.
   */
  public boolean isSecure() {
    return this.scheme.equalsIgnoreCase(SIPS);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();

    sb.append(this.scheme).append(":");

    if (this.userinfo.isPresent()) {
      sb.append(this.userinfo.get());
      sb.append("@");
    }

    sb.append(this.host.toString());

    if (this.parameters != null) {
      sb.append(this.parameters.toString());
    }

    if ((this.headers != null) && !this.headers.isEmpty()) {

      sb.append('?');

      int i = 0;

      for (final RawHeader header : this.headers) {
        if (i++ > 0) {
          sb.append('&');
        }
        sb.append(UrlEscapers.urlFormParameterEscaper().escape(header.name()));
        sb.append('=');
        sb.append(UrlEscapers.urlFormParameterEscaper().escape(header.value()));
      }

    }

    return sb.toString();

  }

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.message.api.uri.Uri#apply(io.rtcore.sip.message.message.api.uri.
   * UriVisitor)
   */
  @Override
  public <T> T apply(final UriVisitor<T> visitor) {
    if (visitor instanceof SipUriVisitor<?>) {
      return ((SipUriVisitor<T>) visitor).visit(this);
    }
    return visitor.visit(this);
  }

  @Override
  public SipUri withParameters(final Parameters parameters) {
    return new SipUri(this.scheme, this.userinfo, this.host, parameters, this.headers);
  }

  public static SipUri create(final HostAndPort host) {
    return new SipUri(host);
  }

  public static SipUri create(final InetSocketAddress self) {
    return create(self.getHostString(), self.getPort());
  }

  public static SipUri create(final String hostname, final int port) {
    return new SipUri(HostAndPort.fromParts(hostname, port));
  }

  public static SipUri fromHost(final String hostname) {
    return new SipUri(HostAndPort.fromString(hostname));
  }

  public static SipUri fromUserAndHost(final String user, final String host) {
    return new SipUri(UserInfo.of(user), HostAndPort.fromString(host));
  }

  public static SipUri fromUserAndHost(final String user, final HostAndPort host) {
    return new SipUri(UserInfo.of(user), host);
  }

  public Optional<String> getUsername() {
    if (this.userinfo.isPresent()) {
      return Optional.ofNullable(this.userinfo.get().user());
    }
    return Optional.empty();
  }

  /**
   * Creates a {@link SipUri} from a {@link TelUri} instance.
   *
   * @param tel
   * @param string
   */

  public static SipUri fromTelUri(final TelUri tel, final HostAndPort host) {
    final StringBuilder user = new StringBuilder();
    user.append(tel.number());
    if (tel.getParameters().isPresent()) {
      user.append(tel.getParameters().get());
    }
    return SipUri.fromUserAndHost(user.toString(), host)
      .withParameter(Token.from("user"),
        Token.from("phone"));
  }

  public SipUri withHeader(final String name, final String value) {
    final Collection<RawHeader> headers = new LinkedList<>(this.headers);
    headers.add(new RawHeader(name, value));
    return new SipUri(this.scheme, this.userinfo, this.host, this.parameters, headers);
  }

  public SipUri withHeaders(final Collection<RawHeader> add) {
    final Collection<RawHeader> headers = new LinkedList<>(this.headers);
    headers.addAll(add);
    return new SipUri(this.scheme, this.userinfo, this.host, this.parameters, headers);
  }

  public SipUri withoutHeaders() {
    return new SipUri(this.scheme, this.userinfo, this.host, this.parameters, null);
  }

  public SipUri withHost(final HostAndPort host) {
    return new SipUri(this.scheme, this.userinfo, host, this.parameters, this.headers);
  }

  public SipUri withUserinfo(final Optional<UserInfo> userinfo) {
    return new SipUri(this.scheme, userinfo, this.host, this.parameters, this.headers);
  }

  public SipUri withScheme(String scheme) {
    return new SipUri(scheme, userinfo, this.host, this.parameters, this.headers);
  }

  public SipUri withUser(final String user) {
    return this.withUserinfo(Optional.of(UserInfo.of(user)));
  }

  /**
   * Returns the "user" parameter, if it's set. Otherwise, an absent optional is returned.
   *
   * @return
   */

  public Optional<String> getUserParameter() {
    if (this.parameters != null) {
      return this.parameters.getParameter(P_USER).map(new Function<Token, String>() {
        @Override
        public String apply(final Token input) {
          return input.toString().toLowerCase();
        }
      });
    }

    return null;
  }

  public SipUri withoutParameter(final SipParameterDefinition<?> param) {
    return this.withoutParameter(param.name());
  }

  /**
   * Fetches a parameter as a simgple string.
   *
   * @param string
   * @return
   */

  public Optional<String> getParameter(final String string) {
    return this.parameters.getParameter(string);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this.canCompare(obj)) {
      final SipUri other = (SipUri) obj;
      // Check scheme
      return this.scheme.equals(other.scheme)
        // Compare user info
        && this.userinfo.equals(other.userinfo)
        // Check host and port
        && this.host.equals(other.host)
        // Check special parameters
        && this.getParameter("user").equals(other.getParameter("user"))
        && this.getParameter("ttl").equals(other.getParameter("ttl"))
        && this.getParameter("method").equals(other.getParameter("method"))
        && this.getParameter("maddr").equals(other.getParameter("maddr"))
        // Check other parameters
        && this.parameters.compareCommonParameters(other.parameters);
    }
    else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.scheme, this.host, this.userinfo, this.parameters, this.headers);
  }

  private boolean canCompare(final Object obj) {
    return obj instanceof SipUri;
  }

  @Override
  public String getScheme() {
    return this.scheme;
  }

  public Optional<UserInfo> getUserinfo() {
    return this.userinfo;
  }

  public HostAndPort getHost() {
    return this.host;
  }

  public Collection<RawHeader> getHeaders() {
    return this.headers;
  }

  public static SipUri parseString(String input) {
    return SipUriParser.parse(input);
  }

  public Optional<SipTransport> transport() {
    return getParameter(PTransport).map(SipTransport::fromToken);
  }

}
