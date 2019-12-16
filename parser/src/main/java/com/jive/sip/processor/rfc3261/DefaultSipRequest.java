package com.jive.sip.processor.rfc3261;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
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
import com.jive.sip.auth.headers.Authorization;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.api.CSeq;
import com.jive.sip.message.api.EventSpec;
import com.jive.sip.message.api.NameAddr;
import com.jive.sip.message.api.RAck;
import com.jive.sip.message.api.Reason;
import com.jive.sip.message.api.Replaces;
import com.jive.sip.message.api.SipHeaderDefinition;
import com.jive.sip.message.api.SipMessage;
import com.jive.sip.message.api.SipMessageVisitor;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.TargetDialog;
import com.jive.sip.message.api.TokenSet;
import com.jive.sip.message.api.headers.RValue;
import com.jive.sip.uri.api.Uri;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class DefaultSipRequest extends DefaultSipMessage implements SipRequest {

  /**
   *
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
      final Collection<RawHeader> headers,
      byte[] body) {
    super(manager, headers);
    this.method = method;
    this.uri = uri;
    this.version = version;
    this.body = body;
  }

  @Override
  public Optional<UnsignedInteger> getMaxForwards() {
    return this.getHeader(MAX_FORWARDS);
  }

  @Override
  public Optional<TokenSet> getProxyRequire() {
    return Optional.empty();
  }

  @Override
  public List<Authorization> getProxyAuthorization() {
    return this.getHeader(PROXY_AUTHORIZATION).orElse(Collections.emptyList());
  }

  @Override
  public List<Authorization> getAuthorization() {
    return this.getHeader(AUTHORIZATION).orElse(Collections.emptyList());
  }

  @Override
  public Optional<UnsignedInteger> getExpires() {
    return this.getHeader(EXPIRES);
  }

  @Override
  public Optional<CharSequence> getUserAgent() {
    return this.getHeader(USER_AGENT);
  }

  @Override
  public Optional<RAck> getRAck() {
    return this.getHeader(RACK);
  }

  @Override
  public String toString() {
    if (this.body != null && this.body.length > 0)
      return String.format("%s %s [%s, %d bytes]", getMethod(), getUri(), this.getContentType().orElse("???"), this.body.length);
    return String.format("%s %s", getMethod(), getUri());
  }

  @Override
  public List<NameAddr> getPath() {
    return this.getHeader(PATH).orElse(Lists.<NameAddr>newArrayList());
  }

  @Override
  public Optional<EventSpec> getEvent() {
    return this.getHeader(EVENT);
  }

  @Override
  public List<NameAddr> getRecordRoute() {
    return this.getHeader(RECORD_ROUTE).orElse(Collections.<NameAddr>emptyList());
  }

  @Override
  public Optional<NameAddr> getPServedUser() {
    return this.getHeader(P_SERVED_USER);
  }

  @Override
  public List<NameAddr> getPAssertedIdentity() {
    return this.getHeader(P_ASSERTED_IDENTITY).orElse(Collections.<NameAddr>emptyList());
  }

  @Override
  public List<RValue> getResourcePriority() {
    return this.getHeader(RESOURCE_PRIORITY).orElse(Collections.emptyList());
  }

  @Override
  public Optional<Replaces> getReplaces() {
    return this.getHeader(REPLACES);
  }

  @Override
  public Optional<NameAddr> getReferTo() {
    return this.getHeader(REFER_TO);
  }

  @Override
  public Optional<NameAddr> getReferredBy() {
    return this.getHeader(REFERRED_BY);
  }

  @Override
  public Optional<Reason> getReason() {
    return this.getHeader(REASON);

  }

  @Override
  public Optional<TokenSet> getRequestDisposition() {
    return this.getHeader(REQUEST_DISPOSITION);
  }

  @Override
  public Optional<TokenSet> getPrivacy() {
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
    final DefaultSipMessage result =
      new DefaultSipRequest(
        this.manager.adapt(RfcSipMessageManager.class),
        this.method,
        this.uri,
        this.version,
        headers,
        this.body);
    return result;
  }

  @Override
  public SipMessage withReplacedHeaders(final RawHeader... headers) {
    // This maintains ordering of headers
    final Map<String, String> replacementValues = Maps.newHashMap();
    final List<RawHeader> headerList = Lists.newArrayList();
    for (final RawHeader h : headers) {
      replacementValues.put(h.getName(), h.getValue());
    }
    for (final RawHeader h : this.headers) {
      if (replacementValues.containsKey(h.getName())) {
        headerList.add(new RawHeader(h.getName(), replacementValues.remove(h.getName())));
      }
      else {
        headerList.add(h);
      }
    }

    for (String name : replacementValues.keySet()) {
      headerList.add(new RawHeader(name, replacementValues.get(name)));
    }

    final DefaultSipMessage result =
      new DefaultSipRequest(
        this.manager.adapt(RfcSipMessageManager.class),
        this.method,
        this.uri,
        this.version,
        headerList,
        this.body);
    return result;
  }

  @Override
  public <T> SipMessage withReplacedHeader(final SipHeaderDefinition<T> header, final T value) {
    return this.withoutHeaders(header).withParsed(header.getName(), Lists.newArrayList(value));
  }

  @Override
  public SipRequest withUri(final Uri uri) {
    final DefaultSipRequest result =
      new DefaultSipRequest(
        this.manager.adapt(RfcSipMessageManager.class),
        this.method,
        uri,
        this.version,
        this.headers,
        this.body);
    return result;
  }

  @Override
  public String getVersion() {
    return this.version;
  }

  @Override
  public SipRequest withMethod(final SipMethod method) {
    final DefaultSipRequest result =
      new DefaultSipRequest(
        this.manager.adapt(RfcSipMessageManager.class),
        method,
        this.uri,
        this.version,
        this.headers,
        this.body);
    return result;
  }

  @Override
  public SipRequest withBody(final byte[] body) {
    final DefaultSipRequest result =
      new DefaultSipRequest(
        this.manager.adapt(RfcSipMessageManager.class),
        this.method,
        this.uri,
        this.version,
        this.headers,
        body);
    return (SipRequest) result.withReplacedHeader(DefaultSipMessage.CONTENT_LENGTH, UnsignedInteger.fromIntBits(body.length));
  }

  @Override
  public SipRequest withBody(final String body) {
    return this.withBody(body.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public DefaultSipRequest withBody(final String contentType, final byte[] body) {
    return (DefaultSipRequest) this.withBody(body).withReplacedHeader(DefaultSipMessage.CONTENT_TYPE, contentType);
  }

  @Override
  public DefaultSipRequest withoutHeaders(final String... headerNames) {
    final List<String> badHeaders = Lists.newArrayList(headerNames);
    final List<RawHeader> keepers = Lists.newArrayList(Iterables.filter(this.headers, header -> !Iterables.contains(badHeaders, header.getName())));

    final DefaultSipRequest result =
      new DefaultSipRequest(
        this.manager.adapt(RfcSipMessageManager.class),
        this.method,
        this.uri,
        this.version,
        keepers,
        this.body);
    return result;
  }

  @Override
  public DefaultSipRequest withoutHeaders(final SipHeaderDefinition... headers) {
    final List<SipHeaderDefinition> headerDefinitionList = Arrays.asList(headers);

    final Set<String> longHeaderNamesToRemove =
      headerDefinitionList.stream()
        .map(SipHeaderDefinition::getName)
        .collect(Collectors.toSet());

    final Set<String> compactHeaderNamesToRemove =
      headerDefinitionList.stream()
        .map(SipHeaderDefinition::getShortName)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map((c) -> c.toString())
        .collect(Collectors.toSet());

    final List<String> headerNamesToRemove = Sets.union(longHeaderNamesToRemove, compactHeaderNamesToRemove).stream().collect(Collectors.toList());

    return this.withoutHeaders(headerNamesToRemove.toArray(new String[headerNamesToRemove.size()]));
  }

  @Override
  public SipRequest withPrepended(final String header, final Object value) {
    final String field = serializer.serialize(value);
    final List<RawHeader> headers = Lists.newArrayList(this.headers);
    headers.add(0, new RawHeader(header, field));
    final DefaultSipRequest result =
      new DefaultSipRequest(
        this.manager.adapt(RfcSipMessageManager.class),
        this.method,
        this.uri,
        this.version,
        headers,
        this.body);
    return result;
  }

  @Override
  public SipRequest withAppended(final String header, final Object value) {
    final String field = serializer.serialize(value);
    final List<RawHeader> headers = Lists.newArrayList(this.headers);
    // find the last index of this header name,

    int last = 0;

    for (int i = 0; i < headers.size(); ++i) {
      final RawHeader raw = headers.get(i);
      if (raw.getName().equals(header)) {
        last = i;
      }
    }

    headers.add(last + 1, new RawHeader(header, field));
    final DefaultSipRequest result =
      new DefaultSipRequest(
        this.manager.adapt(RfcSipMessageManager.class),
        this.method,
        this.uri,
        this.version,
        headers,
        this.body);
    return result;
  }

  @Override
  public <T> SipRequest withParsed(final String name, final List<T> fields) {

    final List<RawHeader> headers = Lists.newArrayList(this.headers);

    for (final Object field : fields) {
      headers.add(new RawHeader(name, serializer.serialize(field)));
    }

    final DefaultSipRequest result =
      new DefaultSipRequest(
        this.manager.adapt(RfcSipMessageManager.class),
        this.method,
        this.uri,
        this.version,
        headers,
        this.body);
    return result;
  }

  @Override
  public SipRequest withPrepended(RawHeader raw) {
    final List<RawHeader> headers = Lists.newLinkedList(this.headers);
    headers.add(0, raw);
    return new DefaultSipRequest(this.manager.adapt(RfcSipMessageManager.class), this.method, this.uri, this.version, headers, this.body);
  }

  @Override
  public SipRequest withFrom(NameAddr na) {
    return this.withoutHeaders("From", "f").withPrepended("From", na);
  }

  @Override
  public SipRequest withTo(NameAddr na) {
    return this.withoutHeaders("To", "t").withPrepended("To", na);
  }

  @Override
  public SipRequest withRoute(List<NameAddr> routeSet) {
    return this.withoutHeaders("Route").withParsed("Route", routeSet);
  }

  @Override
  public DefaultSipRequest withContact(final NameAddr na) {
    return (DefaultSipRequest) withoutHeaders("Contact", "m").withPrepended("Contact", na);
  }

  @Override
  public DefaultSipRequest withContact(final Uri uri) {
    return withContact(new NameAddr(uri));
  }

  @Override
  public SipRequest withCSeq(final long seqNum, final SipMethod method) {
    return this
      .withoutHeaders(CSEQ)
      .withAppended(CSEQ.getName(), new CSeq(seqNum, method));
  }

  @Override
  public SipRequest withIncrementedCSeq(final SipMethod method) {
    return this.withCSeq(this.getCSeq().getSequence().plus(UnsignedInteger.ONE).longValue(), method);
  }

  @Override
  public Optional<TargetDialog> getTargetDialog() {
    return this.getHeader(TARGET_DIALOG);
  }

  @Override
  public SipMethod getMethod() {
    return this.method;
  }

  @Override
  public Uri getUri() {
    return this.uri;
  }

}
