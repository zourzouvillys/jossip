package io.rtcore.sip.message.processor.rfc3261;

import static io.rtcore.sip.message.parsers.core.ParserUtils.TOKEN;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.auth.headers.Authorization;
import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.message.SipMessage;
import io.rtcore.sip.message.message.api.BranchId;
import io.rtcore.sip.message.message.api.CSeq;
import io.rtcore.sip.message.message.api.ContactSet;
import io.rtcore.sip.message.message.api.ContentDisposition;
import io.rtcore.sip.message.message.api.EventSpec;
import io.rtcore.sip.message.message.api.MinSE;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.message.api.RAck;
import io.rtcore.sip.message.message.api.Reason;
import io.rtcore.sip.message.message.api.Replaces;
import io.rtcore.sip.message.message.api.SessionExpires;
import io.rtcore.sip.message.message.api.SipHeaderDefinition;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.message.api.TargetDialog;
import io.rtcore.sip.message.message.api.TokenSet;
import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.message.message.api.headers.CallId;
import io.rtcore.sip.message.message.api.headers.HistoryInfo;
import io.rtcore.sip.message.message.api.headers.MIMEType;
import io.rtcore.sip.message.message.api.headers.ParameterizedUri;
import io.rtcore.sip.message.message.api.headers.RValue;
import io.rtcore.sip.message.message.api.headers.RetryAfter;
import io.rtcore.sip.message.message.api.headers.Version;
import io.rtcore.sip.message.parameters.tools.ParameterUtils;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.core.ParserUtils;
import io.rtcore.sip.message.parsers.core.Utf8ParserHelper;
import io.rtcore.sip.message.processor.rfc3261.message.impl.ContactHeaderDefinition;
import io.rtcore.sip.message.processor.rfc3261.message.impl.HistoryInfoHeaderDefinition;
import io.rtcore.sip.message.processor.rfc3261.message.impl.SingleHeaderDefinition;
import io.rtcore.sip.message.processor.rfc3261.message.impl.TokenSetCollector;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.AuthorizationParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.CSeqParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.CallIdParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.ContentDispositionParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.DateTimeParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.EventParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.MIMETypeParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.MinSEParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.NameAddrParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.ParameterizedUriParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.RAckParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.RValueParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.ReasonParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.ReplacesParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.RetryAfterParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.SessionExpiresParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.TargetDialogParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.VersionParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.ViaParser;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManagerBuilder;
import io.rtcore.sip.message.uri.Uri;

public abstract class DefaultSipMessage implements SipMessage {
  protected static final RfcSerializerManager serializer = new RfcSerializerManagerBuilder().build();
  /**
   */
  private static final long serialVersionUID = 1L;
  private static final Parser<NameAddr> NAME_ADDR_PARSER = new NameAddrParser();
  private static final Parser<Authorization> AUTHORIZATION_PARSER = new AuthorizationParser();
  private static final Parser<MIMEType> MIME_PARSER = new MIMETypeParser();
  // CHECKSTYLE:OFF
  private static final Supplier<TokenSetCollector> TOKEN_SET_BUILDER = new Supplier<TokenSetCollector>() {
    @Override
    public TokenSetCollector get() {
      return new TokenSetCollector();
    }
  };
  public static final SipHeaderDefinition<List<MIMEType>> ACCEPT = MultiHeaderDefinition.create(MIME_PARSER, "Accept");
  public static final SipHeaderDefinition<List<RValue>> ACCEPT_RESOURCE_PRIORITY = MultiHeaderDefinition.create(new RValueParser(), "Accept-Resource-Priority");
  public static final SipHeaderDefinition<List<ParameterizedUri>> ALERT_INFO = MultiHeaderDefinition.create(new ParameterizedUriParser(), "Alert-Info");
  public static final SipHeaderDefinition<TokenSet> ALLOW = MultiHeaderDefinition.create(TOKEN, TOKEN_SET_BUILDER, "Allow");
  public static final SipHeaderDefinition<List<Authorization>> AUTHORIZATION = MultiHeaderDefinition.create(AUTHORIZATION_PARSER, "Authorization");
  public static final SipHeaderDefinition<CSeq> CSEQ = SingleHeaderDefinition.create(new CSeqParser(), "CSeq");
  public static final SipHeaderDefinition<CallId> CALL_ID = SingleHeaderDefinition.create(new CallIdParser(), "Call-ID", 'i');
  public static final SipHeaderDefinition<ContactSet> CONTACT = new ContactHeaderDefinition();
  public static final SipHeaderDefinition<UnsignedInteger> CONTENT_LENGTH = SingleHeaderDefinition.create(ParserUtils.uint(1, 5), "Content-Length", 'l');
  public static final SipHeaderDefinition<ContentDisposition> CONTENT_DISPOSITION =
    SingleHeaderDefinition.create(new ContentDispositionParser(), "Content-Disposition");
  public static final SipHeaderDefinition<String> CONTENT_TYPE = SingleHeaderDefinition.create("Content-Type", 'c');
  public static final SipHeaderDefinition<ZonedDateTime> DATE = SingleHeaderDefinition.create(new DateTimeParser(), "Date");
  public static final SipHeaderDefinition<List<ParameterizedUri>> ERROR_INFO = MultiHeaderDefinition.create(new ParameterizedUriParser(), "Error-Info");
  public static final SipHeaderDefinition<UnsignedInteger> EXPIRES = SingleHeaderDefinition.create(ParserUtils.uint(1, 10), "Expires");
  public static final SipHeaderDefinition<EventSpec> EVENT = SingleHeaderDefinition.create(new EventParser(), "Event", 'o');
  public static final SipHeaderDefinition<NameAddr> FROM = SingleHeaderDefinition.create(NAME_ADDR_PARSER, "From", 'f');
  public static final SipHeaderDefinition<HistoryInfo> HISTORY_INFO = new HistoryInfoHeaderDefinition();
  public static final SipHeaderDefinition<Version> MIME_VERSION = SingleHeaderDefinition.create(new VersionParser(), "MIME-Version");
  public static final SipHeaderDefinition<MinSE> MIN_SE = SingleHeaderDefinition.create(new MinSEParser(), "Min-SE");
  public static final SipHeaderDefinition<UnsignedInteger> MAX_FORWARDS = SingleHeaderDefinition.create(ParserUtils.uint(1, 7), "Max-Forwards");
  public static final SipHeaderDefinition<List<NameAddr>> PATH = MultiHeaderDefinition.create(NAME_ADDR_PARSER, "Path");
  public static final SipHeaderDefinition<TokenSet> PRIVACY = MultiHeaderDefinition.create(TOKEN, TOKEN_SET_BUILDER, "Privacy");
  // public static final SipHeaderDefinition<Credentials> PROXY_AUTHORIZATION = new
  // CredentialsHeaderDefinition(AUTHORIZATION_PARSER, "Proxy-Authorization");
  /**
   * Proxy-Authorization is a multi-header.
   */
  public static final SipHeaderDefinition<List<Authorization>> PROXY_AUTHORIZATION = MultiHeaderDefinition.create(AUTHORIZATION_PARSER, "Proxy-Authorization");
  public static final SipHeaderDefinition<List<Authorization>> PROXY_AUTHENTICATE = MultiHeaderDefinition.create(AUTHORIZATION_PARSER, "Proxy-Authenticate");
  public static final SipHeaderDefinition<TokenSet> PROXY_REQUIRE = MultiHeaderDefinition.create(TOKEN, TOKEN_SET_BUILDER, "Proxy-Require");
  public static final SipHeaderDefinition<List<NameAddr>> P_ASSERTED_IDENTITY = MultiHeaderDefinition.create(NAME_ADDR_PARSER, "P-Asserted-Identity");
  public static final SipHeaderDefinition<NameAddr> P_SERVED_USER = SingleHeaderDefinition.create(NAME_ADDR_PARSER, "P-Served-User");
  public static final SipHeaderDefinition<UnsignedInteger> RSEQ = SingleHeaderDefinition.create(ParserUtils.uint(1, 10), "RSeq");
  public static final SipHeaderDefinition<RAck> RACK = SingleHeaderDefinition.create(new RAckParser(), "RAck");
  public static final SipHeaderDefinition<List<NameAddr>> RECORD_ROUTE = MultiHeaderDefinition.create(NAME_ADDR_PARSER, "Record-Route");
  public static final SipHeaderDefinition<Reason> REASON = SingleHeaderDefinition.create(new ReasonParser(), "Reason");
  public static final SipHeaderDefinition<NameAddr> REFER_TO = SingleHeaderDefinition.create(NAME_ADDR_PARSER, "Refer-To", 'r');
  public static final SipHeaderDefinition<NameAddr> REFERRED_BY = SingleHeaderDefinition.create(NAME_ADDR_PARSER, "Referred-By", 'b');
  public static final SipHeaderDefinition<Replaces> REPLACES = SingleHeaderDefinition.create(new ReplacesParser(), "Replaces");
  public static final SipHeaderDefinition<RetryAfter> RETRY_AFTER = SingleHeaderDefinition.create(new RetryAfterParser(), "Retry-After");
  public static final SipHeaderDefinition<TokenSet> REQUIRE = MultiHeaderDefinition.create(TOKEN, TOKEN_SET_BUILDER, "Require");
  public static final SipHeaderDefinition<TokenSet> REQUEST_DISPOSITION = MultiHeaderDefinition.create(TOKEN, TOKEN_SET_BUILDER, "Request-Disposition");
  public static final SipHeaderDefinition<List<RValue>> RESOURCE_PRIORITY = MultiHeaderDefinition.create(new RValueParser(), "Resource-Priority");
  public static final SipHeaderDefinition<List<NameAddr>> ROUTE = MultiHeaderDefinition.create(NAME_ADDR_PARSER, "Route");
  public static final SipHeaderDefinition<CharSequence> SERVER = SingleHeaderDefinition.create(ParserUtils.all(), "Server");
  public static final SipHeaderDefinition<String> SESSION_ID = SingleHeaderDefinition.create(ParserUtils.allString(), "Session-ID");
  public static final SipHeaderDefinition<SessionExpires> SESSION_EXPIRES = SingleHeaderDefinition.create(new SessionExpiresParser(), "Session-Expires", 'x');
  public static final SipHeaderDefinition<CharSequence> SUBJECT =
    SingleHeaderDefinition.create(ParserUtils.optional(Utf8ParserHelper.TEXT_UTF8_TRIM), "Subject", 's');
  public static final SipHeaderDefinition<TokenSet> SUPPORTED = MultiHeaderDefinition.create(TOKEN, TOKEN_SET_BUILDER, "Supported", 'k');
  public static final SipHeaderDefinition<TargetDialog> TARGET_DIALOG = SingleHeaderDefinition.create(new TargetDialogParser(), "Target-Dialog");
  public static final SipHeaderDefinition<NameAddr> TO = SingleHeaderDefinition.create(NAME_ADDR_PARSER, "To", 't');
  public static final SipHeaderDefinition<TokenSet> UNSUPPORTED = MultiHeaderDefinition.create(TOKEN, TOKEN_SET_BUILDER, "Unsupported");
  public static final SipHeaderDefinition<CharSequence> USER_AGENT = SingleHeaderDefinition.create(ParserUtils.all(), "User-Agent");
  public static final SipHeaderDefinition<List<Via>> VIA = MultiHeaderDefinition.create(new ViaParser(), "Via", 'v');
  public static final SipHeaderDefinition<List<Authorization>> WWW_AUTHENTICATE = MultiHeaderDefinition.create(AUTHORIZATION_PARSER, "WWW-Authenticate");
  // CHECKSTYLE.ON
  protected byte[] body;
  protected Collection<RawHeader> headers;
  protected final SipMessageManager manager;
  private LoadingCache<SipHeaderDefinition<?>, Optional<?>> parsedHeaders;

  public DefaultSipMessage(final SipMessageManager manager) {
    this(manager, null);
  }

  @Override
  public byte[] body() {
    return this.body;
  }

  public SipMessageManager getSipMessageManager() {
    return this.manager;
  }

  public DefaultSipMessage(final SipMessageManager manager, final Collection<RawHeader> headers) {
    this(manager, headers, null);
  }

  public DefaultSipMessage(final SipMessageManager manager, final Collection<RawHeader> headers, final byte[] body) {
    this.manager = manager;
    this.headers = headers;
    setBody(body);
    final DefaultSipMessage message = this;
    this.parsedHeaders = CacheBuilder.newBuilder().build(new CacheLoader<SipHeaderDefinition<?>, Optional<?>>() {
      @Override
      public Optional<?> load(final SipHeaderDefinition<?> definition) {
        return Optional.ofNullable(definition.parse(message.headers));
      }
    });
  }

  public void setBody(final byte[] body) {
    if (body != null) {
      this.body = Arrays.copyOf(body, body.length);
    }
    else {
      this.body = null;
    }
  }

  public void addHeader(final RawHeader header) {
    if (this.headers == null) {
      this.headers = Lists.newLinkedList();
    }
    this.headers.add(header);
  }

  @Override
  public List<RawHeader> headers() {
    if (this.headers == null) {
      return ImmutableList.of();
    }
    return ImmutableList.copyOf(this.headers);
  }

  @Override
  public Optional<RawHeader> getHeader(String name) {
    name = name.toLowerCase();
    for (final RawHeader header : headers()) {
      if (name.toLowerCase().equals(header.name().toLowerCase())) {
        return Optional.of(header);
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<String> contentType() {
    return this.getHeader(CONTENT_TYPE);
  }

  @Override
  public Optional<ContentDisposition> contentDisposition() {
    return this.getHeader(CONTENT_DISPOSITION);
  }

  @Override
  public NameAddr to() {
    return getHeader(TO).orElse(null);
  }

  @Override
  public NameAddr from() {
    return getHeader(FROM).orElse(null);
  }

  @Override
  public HistoryInfo historyInfo() {
    return getHeader(HISTORY_INFO).orElse(HistoryInfo.EMPTY);
  }

  @Override
  public List<ParameterizedUri> alertInfo() {
    return getHeader(ALERT_INFO).orElse(Collections.emptyList());
  }

  @Override
  public Optional<String> sessionId() {
    return this.getHeader(SESSION_ID);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> Optional<T> getHeader(final SipHeaderDefinition<T> def) {
    try {
      return (Optional<T>) this.parsedHeaders.get(def);
    }
    catch (final ExecutionException e) {
      Throwables.throwIfUnchecked(e.getCause());
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<RawHeader> getHeaders(final String... name) {
    final List<RawHeader> ret = Lists.newLinkedList();
    final Set<String> names = Sets.newHashSet(name);
    for (final RawHeader header : headers()) {
      if (names.contains(header.name())) {
        ret.add(header);
      }
    }
    return ret;
  }

  @SuppressWarnings("unchecked")
  protected <T> List<T> getHeaderList(final Class<T> type, final String name) {
    // TODO Fix this method since it doesn't work as advertised - jhutchins & zmorin
    final List<T> result = new ArrayList<T>();
    for (final RawHeader header : headers()) {
      if (name.equals(header.name())) {
        result.add((T) header);
      }
    }
    return result;
  }

  @Override
  public String toTag() {
    final NameAddr to = to();
    if (to != null) {
      final Optional<Token> tag = to.getParameter(ParameterUtils.Tag);
      if (tag.isPresent()) {
        return tag.get().toString();
      }
    }
    return null;
  }

  @Override
  public Uri toAddress() {
    return to().address();
  }

  // TODO: this should return an optional
  @Override
  public String fromTag() {
    final Token val = from().getParameter(ParameterUtils.Tag).orElse(null);
    if (val != null) {
      return val.toString();
    }
    return "";
  }

  @Override
  public Optional<ContactSet> contacts() {
    return this.getHeader(CONTACT);
  }

  @Override
  public CallId callId() {
    return this.getHeader(CALL_ID).orElse(null);
  }

  // TODO: return optional
  @Override
  public CSeq cseq() {
    return this.getHeader(CSEQ).orElse(null);
  }

  @Override
  public List<NameAddr> route() {
    return this.getHeader(ROUTE).orElse(Collections.<NameAddr>emptyList());
  }

  @Override
  public List<NameAddr> recordRoute() {
    return this.getHeader(RECORD_ROUTE).orElse(Collections.<NameAddr>emptyList());
  }

  @Override
  public List<Via> vias() {
    return this.getHeader(VIA).orElse(Collections.<Via>emptyList());
  }

  @Override
  public Optional<TokenSet> allow() {
    return this.getHeader(ALLOW);
  }

  @Override
  public Optional<TokenSet> supported() {
    return this.getHeader(SUPPORTED);
  }

  @Override
  public Optional<TokenSet> require() {
    return this.getHeader(REQUIRE);
  }

  @Override
  public Optional<List<MIMEType>> accept() {
    return this.getHeader(ACCEPT);
  }

  @Override
  public Optional<SessionExpires> sessionExpires() {
    return this.getHeader(SESSION_EXPIRES);
  }

  @Override
  public Optional<MinSE> minSE() {
    return this.getHeader(MIN_SE);
  }

  @Override
  public void validate() {
    for (final RawHeader header : this.headers) {
      // TODO remove the need for a RFCSipMessageManager here. - Hutch
      final SipHeaderDefinition<?> parser = this.manager.adapt(RfcSipMessageManager.class).getParser(header.name(), null);
      if (parser != null) {
        this.getHeader(parser);
      }
    }
  }

  @Override
  public BranchId branchId() {
    final List<Via> vias = vias();
    if (vias.isEmpty()) {
      return null;
    }
    else {
      final Token branch = vias.get(0).getParameter(ParameterUtils.Branch).orElse(null);
      if (branch == null) {
        return null;
      }
      else {
        return BranchId.fromToken(branch);
      }
    }
  }

  public void addHeader(final String name, final String rawValue, final Object parsedValue) {
    // TODO remove the need for a RFCSipMessageManager here. - Hutch
    final SipHeaderDefinition<?> parser = this.manager.adapt(RfcSipMessageManager.class).getParser(name, null);
    if ((parsedValue != null) && (parser != null)) {
      this.parsedHeaders.put(parser, Optional.of(parsedValue));
    }
    this.addHeader(new RawHeader(name, rawValue));
  }

  @Override
  public SipMessage withCSeq(final long seqNum, final SipMethod method) {
    return this.withoutHeaders(CSEQ).withAppended(CSEQ.getName(), new CSeq(seqNum, method));
  }

  @Override
  public SipMessage withIncrementedCSeq(final SipMethod method) {
    return this.withCSeq(this.cseq().sequence().plus(UnsignedInteger.ONE).longValue(), method);
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof DefaultSipMessage))
      return false;
    final DefaultSipMessage other = (DefaultSipMessage) o;
    if (!other.canEqual((Object) this))
      return false;
    if (!java.util.Arrays.equals(this.body(), other.body()))
      return false;
    final Object this$headers = this.headers();
    final Object other$headers = other.headers();
    if (this$headers == null ? other$headers != null
                             : !this$headers.equals(other$headers))
      return false;
    final Object this$manager = this.manager;
    final Object other$manager = other.manager;
    if (this$manager == null ? other$manager != null
                             : !this$manager.equals(other$manager))
      return false;
    final Object this$parsedHeaders = this.parsedHeaders;
    final Object other$parsedHeaders = other.parsedHeaders;
    if (this$parsedHeaders == null ? other$parsedHeaders != null
                                   : !this$parsedHeaders.equals(other$parsedHeaders))
      return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof DefaultSipMessage;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = (result * PRIME) + java.util.Arrays.hashCode(this.body());
    final Object $headers = this.headers();
    result =
      (result * PRIME)
        + ($headers == null ? 43
                            : $headers.hashCode());
    final Object $manager = this.manager;
    result =
      (result * PRIME)
        + ($manager == null ? 43
                            : $manager.hashCode());
    final Object $parsedHeaders = this.parsedHeaders;
    result =
      (result * PRIME)
        + ($parsedHeaders == null ? 43
                                  : $parsedHeaders.hashCode());
    return result;
  }
}
