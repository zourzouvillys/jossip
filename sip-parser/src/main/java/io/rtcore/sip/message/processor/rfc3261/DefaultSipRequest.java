package io.rtcore.sip.message.processor.rfc3261;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.auth.headers.Authorization;
import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.message.SipMessage;
import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.api.CSeq;
import io.rtcore.sip.message.message.api.EventSpec;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.message.api.RAck;
import io.rtcore.sip.message.message.api.Reason;
import io.rtcore.sip.message.message.api.Replaces;
import io.rtcore.sip.message.message.api.SipHeaderDefinition;
import io.rtcore.sip.message.message.api.SipMessageVisitor;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.message.api.TargetDialog;
import io.rtcore.sip.message.message.api.TokenSet;
import io.rtcore.sip.message.message.api.headers.CallId;
import io.rtcore.sip.message.message.api.headers.MIMEType;
import io.rtcore.sip.message.message.api.headers.RValue;
import io.rtcore.sip.message.uri.Uri;

public final class DefaultSipRequest extends DefaultSipMessage implements SipRequest {
  /**
   */
  private static final long serialVersionUID = 1L;
  private final SipMethod method;
  private final String version;
  private final Uri uri;

  public DefaultSipRequest(final RfcSipMessageManager manager, final SipMethod method, final Uri uri) {
    this(manager, method, uri, VERSION);
  }

  public DefaultSipRequest(final RfcSipMessageManager manager, final SipMethod method, final Uri uri, final String version) {
    this(manager, method, uri, version, Lists.<RawHeader>newLinkedList(), null);
  }

  public DefaultSipRequest(
      final RfcSipMessageManager manager,
      final SipMethod method,
      final Uri uri,
      final String version,
      final Iterable<RawHeader> headers,
      final byte[] body) {
    super(manager, headers);
    this.method = method;
    this.uri = uri;
    this.version = version;
    this.body = body;
  }

  @Override
  public Optional<UnsignedInteger> maxForwards() {
    return this.getHeader(MAX_FORWARDS);
  }

  @Override
  public Optional<TokenSet> proxyRequire() {
    return Optional.empty();
  }

  @Override
  public List<Authorization> proxyAuthorization() {
    return this.getHeader(PROXY_AUTHORIZATION).orElse(Collections.emptyList());
  }

  @Override
  public List<Authorization> authorization() {
    return this.getHeader(AUTHORIZATION).orElse(Collections.emptyList());
  }

  @Override
  public Optional<UnsignedInteger> expires() {
    return this.getHeader(EXPIRES);
  }

  @Override
  public Optional<CharSequence> userAgent() {
    return this.getHeader(USER_AGENT);
  }

  @Override
  public Optional<RAck> rack() {
    return this.getHeader(RACK);
  }

  @Override
  public String toString() {
    if ((this.body != null) && (this.body.length > 0)) {
      return String.format("%s %s [%s, %d bytes]", this.method(), this.uri(), this.contentType(), this.body.length);
    }
    return String.format("%s %s", this.method(), this.uri());
  }

  @Override
  public List<NameAddr> path() {
    return this.getHeader(PATH).orElse(Lists.<NameAddr>newArrayList());
  }

  @Override
  public Optional<EventSpec> event() {
    return this.getHeader(EVENT);
  }

  @Override
  public List<NameAddr> recordRoute() {
    return this.getHeader(RECORD_ROUTE).orElse(Collections.<NameAddr>emptyList());
  }

  @Override
  public Optional<NameAddr> pServedUser() {
    return this.getHeader(P_SERVED_USER);
  }

  @Override
  public List<NameAddr> pAssertedIdentity() {
    return this.getHeader(P_ASSERTED_IDENTITY).orElse(Collections.<NameAddr>emptyList());
  }

  @Override
  public List<RValue> resourcePriority() {
    return this.getHeader(RESOURCE_PRIORITY).orElse(Collections.emptyList());
  }

  @Override
  public Optional<Replaces> replaces() {
    return this.getHeader(REPLACES);
  }

  @Override
  public Optional<NameAddr> referTo() {
    return this.getHeader(REFER_TO);
  }

  @Override
  public Optional<NameAddr> referredBy() {
    return this.getHeader(REFERRED_BY);
  }

  @Override
  public Optional<Reason> reason() {
    return this.getHeader(REASON);
  }

  @Override
  public Optional<TokenSet> requestDisposition() {
    return this.getHeader(REQUEST_DISPOSITION);
  }

  @Override
  public Optional<TokenSet> privacy() {
    return this.getHeader(PRIVACY);
  }

  @Override
  public void accept(final SipMessageVisitor visitor) throws IOException {
    visitor.visit(this);
  }

  @Override
  public SipMessage withHeader(final RawHeader header) {
    final List<RawHeader> headers = Lists.newLinkedList();
    headers.addAll(this.headers);
    headers.add(header);
    return new DefaultSipRequest(this.manager.adapt(RfcSipMessageManager.class), this.method, this.uri, this.version, headers, this.body);
  }

  @Override
  public SipMessage withReplacedHeaders(final RawHeader... headers) {
    // This maintains ordering of headers
    final Map<String, String> replacementValues = Maps.newHashMap();
    final List<RawHeader> headerList = Lists.newArrayList();
    for (final RawHeader h : headers) {
      replacementValues.put(h.name(), h.value());
    }
    for (final RawHeader h : this.headers) {
      if (replacementValues.containsKey(h.name())) {
        headerList.add(new RawHeader(h.name(), replacementValues.remove(h.name())));
      }
      else {
        headerList.add(h);
      }
    }
    for (final String name : replacementValues.keySet()) {
      headerList.add(new RawHeader(name, replacementValues.get(name)));
    }
    return new DefaultSipRequest(this.manager.adapt(RfcSipMessageManager.class), this.method, this.uri, this.version, headerList, this.body);
  }

  @Override
  public <T> SipRequest withReplacedHeader(final SipHeaderDefinition<T> header, final T value) {
    return this.withoutHeaders(header).withParsed(header.getName(), Lists.newArrayList(value));
  }

  @Override
  public SipRequest withUri(final Uri uri) {
    return new DefaultSipRequest(this.manager.adapt(RfcSipMessageManager.class), this.method, uri, this.version, this.headers, this.body);
  }

  @Override
  public String version() {
    return this.version;
  }

  @Override
  public SipRequest withMethod(final SipMethod method) {
    return new DefaultSipRequest(this.manager.adapt(RfcSipMessageManager.class), method, this.uri, this.version, this.headers, this.body);
  }

  @Override
  public SipRequest withBody(final byte[] body) {
    final DefaultSipRequest result =
        new DefaultSipRequest(this.manager.adapt(RfcSipMessageManager.class), this.method, this.uri, this.version, this.headers, body);
    return result.withReplacedHeader(DefaultSipMessage.CONTENT_LENGTH, UnsignedInteger.fromIntBits(body.length));
  }

  @Override
  public SipRequest withBody(final String body) {
    return this.withBody(body.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public DefaultSipRequest withBody(final MIMEType contentType, final byte[] body) {
    return (DefaultSipRequest) this.withBody(body).withReplacedHeader(DefaultSipMessage.CONTENT_TYPE, contentType);
  }

  @Override
  public DefaultSipRequest withoutHeaders(final String... headerNames) {
    final List<String> badHeaders = Lists.newArrayList(headerNames);
    final List<RawHeader> keepers = Lists.newArrayList(Iterables.filter(this.headers, header -> !Iterables.contains(badHeaders, header.name())));
    return new DefaultSipRequest(this.manager.adapt(RfcSipMessageManager.class), this.method, this.uri, this.version, keepers, this.body);
  }

  @Override
  public DefaultSipRequest withoutHeaders(final SipHeaderDefinition<?>... headers) {
    final List<SipHeaderDefinition<?>> headerDefinitionList = Arrays.asList(headers);
    final Set<String> longHeaderNamesToRemove = headerDefinitionList.stream().map(SipHeaderDefinition::getName).collect(Collectors.toSet());
    final Set<String> compactHeaderNamesToRemove =
        headerDefinitionList.stream()
        .map(SipHeaderDefinition::getShortName)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(Object::toString)
        .collect(Collectors.toSet());
    final List<String> headerNamesToRemove = Sets.union(longHeaderNamesToRemove, compactHeaderNamesToRemove).stream().collect(Collectors.toList());
    return this.withoutHeaders(headerNamesToRemove.toArray(new String[headerNamesToRemove.size()]));
  }

  @Override
  public SipRequest withPrepended(final String header, final Object value) {
    final String field = serializer.serializeValueToString(value);
    final List<RawHeader> headers = Lists.newArrayList(this.headers);
    headers.add(0, new RawHeader(header, field));
    return new DefaultSipRequest(this.manager.adapt(RfcSipMessageManager.class), this.method, this.uri, this.version, headers, this.body);
  }

  @Override
  public SipRequest withAppended(final String header, final Object value) {
    final String field = serializer.serializeValueToString(value);
    final List<RawHeader> headers = Lists.newArrayList(this.headers);
    // find the last index of this header name,
    int last = 0;
    for (int i = 0; i < headers.size(); ++i) {
      final RawHeader raw = headers.get(i);
      if (raw.name().equals(header)) {
        last = i;
      }
    }
    headers.add(last + 1, new RawHeader(header, field));
    return new DefaultSipRequest(this.manager.adapt(RfcSipMessageManager.class), this.method, this.uri, this.version, headers, this.body);
  }

  @Override
  public <T> SipRequest withParsed(final String name, final List<T> fields) {
    final List<RawHeader> headers = Lists.newArrayList(this.headers);
    for (final Object field : fields) {
      headers.add(new RawHeader(name, serializer.serialize(field)));
    }
    return new DefaultSipRequest(this.manager.adapt(RfcSipMessageManager.class), this.method, this.uri, this.version, headers, this.body);
  }

  @Override
  public SipRequest withPrepended(final RawHeader raw) {
    final List<RawHeader> headers = Lists.newLinkedList(this.headers);
    headers.add(0, raw);
    return new DefaultSipRequest(this.manager.adapt(RfcSipMessageManager.class), this.method, this.uri, this.version, headers, this.body);
  }

  @Override
  public SipRequest withFrom(final NameAddr na) {
    return this.withoutHeaders("From", "f").withPrepended("From", na);
  }

  @Override
  public SipRequest withTo(final NameAddr na) {
    return this.withoutHeaders("To", "t").withPrepended("To", na);
  }

  @Override
  public SipRequest withRoute(final List<NameAddr> routeSet) {
    return this.withoutHeaders("Route").withParsed("Route", routeSet);
  }

  @Override
  public DefaultSipRequest withContact(final NameAddr na) {
    return (DefaultSipRequest) this.withoutHeaders("Contact", "m").withPrepended("Contact", na);
  }

  @Override
  public SipRequest withPrependedRecordRoute(final NameAddr route) {
    return this.withPrepended(RECORD_ROUTE.getName(), route);
  }

  @Override
  public DefaultSipRequest withContact(final Uri uri) {
    return this.withContact(new NameAddr(uri));
  }

  @Override
  public SipRequest withCSeq(final long seqNum, final SipMethod method) {
    return this.withoutHeaders(CSEQ).withAppended(CSEQ.getName(), new CSeq(seqNum, method));
  }

  @Override
  public SipRequest withIncrementedCSeq(final SipMethod method) {
    return this.withCSeq(this.cseq().sequence().plus(UnsignedInteger.ONE).longValue(), method);
  }

  @Override
  public SipRequest withCallId(final String value) {
    return this
        .withoutHeaders(CALL_ID)
        .withAppended(CALL_ID.getName(), new CallId(value));
  }

  @Override
  public SipRequest withMaxForwards(final int value) {
    return this
        .withoutHeaders(MAX_FORWARDS)
        .withAppended(MAX_FORWARDS.getName(), UnsignedInteger.valueOf(value));
  }

  @Override
  public Optional<TargetDialog> targetDialog() {
    return this.getHeader(TARGET_DIALOG);
  }

  @Override
  public SipMethod method() {
    return this.method;
  }

  @Override
  public Uri uri() {
    return this.uri;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof final DefaultSipRequest other) || !other.canEqual(this) || !super.equals(o)) {
      return false;
    }
    final Object this$method = this.method();
    final Object other$method = other.method();
    if (this$method == null ? other$method != null
        : !this$method.equals(other$method)) {
      return false;
    }
    final Object this$version = this.version();
    final Object other$version = other.version();
    if (this$version == null ? other$version != null
        : !this$version.equals(other$version)) {
      return false;
    }
    final Object this$uri = this.uri();
    final Object other$uri = other.uri();
    if (this$uri == null ? other$uri != null
        : !this$uri.equals(other$uri)) {
      return false;
    }
    return true;
  }

  @Override
  protected boolean canEqual(final Object other) {
    return other instanceof DefaultSipRequest;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = super.hashCode();
    final Object $method = this.method();
    result =
        (result * PRIME)
        + ($method == null ? 43
                           : $method.hashCode());
    final Object $version = this.version();
    result =
        (result * PRIME)
        + ($version == null ? 43
                            : $version.hashCode());
    final Object $uri = this.uri();
    return (result * PRIME)
        + ($uri == null ? 43
                        : $uri.hashCode());
  }

  @Override
  public boolean isRequest() {
    return true;
  }

  @Override
  public boolean isResponse() {
    return false;
  }

}
