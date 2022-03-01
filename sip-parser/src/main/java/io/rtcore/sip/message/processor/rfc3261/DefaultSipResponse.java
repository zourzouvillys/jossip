package io.rtcore.sip.message.processor.rfc3261;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.auth.headers.Authorization;
import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.message.SipMessage;
import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.SipResponseStatus;
import io.rtcore.sip.message.message.api.CSeq;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.message.api.SipHeaderDefinition;
import io.rtcore.sip.message.message.api.SipMessageVisitor;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.message.api.headers.CallId;
import io.rtcore.sip.message.message.api.headers.MIMEType;
import io.rtcore.sip.message.message.api.headers.RValue;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManagerBuilder;
import io.rtcore.sip.message.uri.Uri;

public final class DefaultSipResponse extends DefaultSipMessage implements SipResponse {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private final SipResponseStatus status;
  private final String version;

  public DefaultSipResponse(final int code) {
    this(SipResponseStatus.fromCode(code));
  }

  public DefaultSipResponse(final int code, String reason) {
    this(SipResponseStatus.fromCode(code).withReason(reason));
  }

  public DefaultSipResponse(final SipResponseStatus status) {
    this(RfcSipMessageManager.defaultInstance(), status);
  }

  public DefaultSipResponse(final SipMessageManager manager, final String version, final SipResponseStatus status) {
    this(manager, version, status, List.of());
  }

  public DefaultSipResponse(final SipMessageManager manager, final SipResponseStatus status) {
    this(manager, VERSION, status);
  }

  public DefaultSipResponse(
      final SipMessageManager manager,
      final String version,
      final SipResponseStatus status,
      final Iterable<RawHeader> headers) {
    this(manager, version, status, headers, null);
  }

  public DefaultSipResponse(
      final SipResponseStatus status,
      final Iterable<RawHeader> headers,
      final byte[] body) {
    this(
      RfcSipMessageManager.defaultInstance(),
      VERSION,
      status,
      headers,
      body);
  }

  public DefaultSipResponse(
      final SipMessageManager manager,
      final String version,
      final SipResponseStatus status,
      final Iterable<RawHeader> headers,
      final byte[] body) {
    super(manager, headers, body);
    this.status = status;
    this.version = version;
  }

  @Override
  public String toString() {

    final String bodyString;

    if ((this.body != null) && (this.body.length > 0)) {
      bodyString = String.format(" [%s, %d bytes]", this.contentType(), this.body.length);
    }
    else {
      bodyString = "";
    }

    if (this.cseq() == null) {
      return String.format("%d %s",
        this.getStatus().code(),
        this.getStatus().reason()) + bodyString;
    }
    return String.format("%d %s (%d %s)",
      this.getStatus().code(),
      this.getStatus().reason(),
      this.cseq().sequence().longValue(),
      this.cseq().method()) + bodyString;
  }

  @Override
  public void accept(final SipMessageVisitor visitor) {
    try {
      visitor.visit(this);
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public SipMessage withHeader(final RawHeader header) {
    final List<RawHeader> headers = Lists.newLinkedList();
    headers.add(header);
    headers.addAll(this.headers);
    final DefaultSipResponse result =
      new DefaultSipResponse(
        this.manager.adapt(RfcSipMessageManager.class),
        this.version,
        this.status,
        headers);
    result.body = this.body;
    return result;
  }

  @Override
  public String version() {
    return this.version;
  }

  @Override
  public OptionalLong getRSeq() {
    return this.getHeader(DefaultSipMessage.RSEQ).map(val -> OptionalLong.of(val.longValue())).orElse(OptionalLong.empty());
  }

  @Override
  public Optional<String> getServer() {
    return this.getHeader(DefaultSipMessage.SERVER).map(CharSequence::toString);
  }

  @Override
  public List<RValue> getAcceptResourcePriority() {
    return this.getHeader(DefaultSipMessage.ACCEPT_RESOURCE_PRIORITY).orElse(Collections.emptyList());
  }

  @Override
  public List<Authorization> getWWWAuthenticate() {
    return this.getHeader(DefaultSipMessage.WWW_AUTHENTICATE).orElse(Collections.emptyList());
  }

  @Override
  public List<Authorization> getProxyAuthenticate() {
    return this.getHeader(PROXY_AUTHENTICATE).orElse(Collections.emptyList());
  }

  @Override
  public DefaultSipResponse withoutHeaders(final String... headerNames) {
    final List<String> badHeaders = Lists.newArrayList(headerNames);
    final List<RawHeader> keepers = Lists.newArrayList(Iterables.filter(this.headers, header -> !Iterables.contains(badHeaders, header.name())));
    final DefaultSipResponse result = new DefaultSipResponse(this.manager.adapt(RfcSipMessageManager.class), this.version, this.status, keepers);
    result.body = this.body;
    return result;
  }

  @Override
  public DefaultSipResponse withoutHeaders(final SipHeaderDefinition<?>... headers) {
    final List<SipHeaderDefinition<?>> headerDefinitionList = Arrays.asList(headers);

    final Set<String> longHeaderNamesToRemove =
      headerDefinitionList.stream()
        .map(SipHeaderDefinition::getName)
        .collect(Collectors.toSet());

    final Set<String> compactHeaderNamesToRemove =
      headerDefinitionList.stream()
        .map(SipHeaderDefinition::getShortName)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(c -> c.toString())
        .collect(Collectors.toSet());

    final List<String> headerNamesToRemove =
      Sets
        .union(longHeaderNamesToRemove, compactHeaderNamesToRemove)
        .stream()
        .collect(Collectors.toList());

    return this.withoutHeaders(headerNamesToRemove.toArray(new String[headerNamesToRemove.size()]));
  }

  @Override
  public <T> DefaultSipResponse withParsed(final String name, final List<T> fields) {

    final List<RawHeader> headers = Lists.newArrayList(this.headers);

    final RfcSerializerManager serializer = new RfcSerializerManagerBuilder().build();

    for (final Object field : fields) {
      headers.add(new RawHeader(name, serializer.serialize(field)));
    }

    return new DefaultSipResponse(this.manager.adapt(RfcSipMessageManager.class), this.version, this.status, headers, this.body);
  }

  public SipResponse withBody(final byte[] body) {
    final DefaultSipResponse result =
      new DefaultSipResponse(
        this.manager.adapt(RfcSipMessageManager.class),
        this.version,
        this.status,
        this.headers);
    result.body = body;
    return result.withReplacedHeader(DefaultSipMessage.CONTENT_LENGTH, UnsignedInteger.fromIntBits(body.length));
  }

  public SipResponse withBody(final String body) {
    return this.withBody(body.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public DefaultSipResponse withBody(final MIMEType contentType, final byte[] body) {
    return (DefaultSipResponse) this.withBody(body).withReplacedHeader(DefaultSipMessage.CONTENT_TYPE, contentType);
  }

  @Override
  public DefaultSipResponse withPrepended(final RawHeader raw) {
    final List<RawHeader> headers = Lists.newLinkedList(this.headers);
    headers.add(0, raw);
    return new DefaultSipResponse(this.manager.adapt(RfcSipMessageManager.class), this.version, this.status, headers, this.body);
  }

  @Override
  public DefaultSipResponse withPrepended(final String header, final Object value) {
    final String field = serializer.serialize(value);
    final List<RawHeader> headers = Lists.newArrayList(this.headers);
    headers.add(0, new RawHeader(header, field));
    return new DefaultSipResponse(
      this.manager.adapt(RfcSipMessageManager.class),
      this.version,
      this.status,
      headers,
      this.body);
  }

  @Override
  public DefaultSipResponse withAppended(final String header, final Object value) {
    final String field = serializer.serialize(value);
    final List<RawHeader> headers = Lists.newArrayList(this.headers);
    headers.add(new RawHeader(header, field));
    return new DefaultSipResponse(
      this.manager.adapt(RfcSipMessageManager.class),
      this.version,
      this.status,
      headers,
      this.body);
  }

  @Override
  public <T> SipResponse withReplacedHeader(final SipHeaderDefinition<T> header, final T value) {
    return this.withoutHeaders(header).withParsed(header.getName(), Lists.newArrayList(value));
  }

  @Override
  public DefaultSipResponse withFrom(final NameAddr na) {
    return this.withoutHeaders("From", "f").withPrepended("From", na);
  }

  @Override
  public DefaultSipResponse withTo(final NameAddr na) {
    return this.withoutHeaders("To", "t").withPrepended("To", na);
  }

  @Override
  public DefaultSipResponse withContact(final NameAddr na) {
    return this.withoutHeaders("Contact", "m").withPrepended("Contact", na);
  }

  @Override
  public DefaultSipResponse withContact(final Uri uri) {
    return this.withContact(new NameAddr(uri));
  }

  @Override
  public SipResponse withCSeq(final long seqNum, final SipMethod method) {
    return this
      .withoutHeaders(CSEQ)
      .withAppended(CSEQ.getName(), new CSeq(seqNum, method));
  }

  @Override
  public SipResponse withIncrementedCSeq(final SipMethod method) {
    return this.withCSeq(
      this.cseq().sequence().plus(UnsignedInteger.ONE).longValue(),
      method);
  }

  @Override
  public SipResponse withCallId(final String value) {
    return this
      .withoutHeaders(CALL_ID)
      .withAppended(CALL_ID.getName(), new CallId(value));
  }

  @Override
  public SipResponse withServer(final String value) {
    return this
      .withoutHeaders(SERVER)
      .withAppended(SERVER.getName(), value);
  }

  @Override
  public SipResponseStatus getStatus() {
    return this.status;
  }

  @Override
  public boolean isRequest() {
    return false;
  }

  @Override
  public boolean isResponse() {
    return true;
  }

}
