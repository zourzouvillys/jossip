package io.rtcore.sip.message.message;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.UnaryOperator;

import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.common.iana.SipMethodId;
import io.rtcore.sip.message.auth.headers.Authorization;
import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.message.api.EventSpec;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.message.api.RAck;
import io.rtcore.sip.message.message.api.Reason;
import io.rtcore.sip.message.message.api.Replaces;
import io.rtcore.sip.message.message.api.SipHeaderDefinition;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.message.api.TargetDialog;
import io.rtcore.sip.message.message.api.TokenSet;
import io.rtcore.sip.message.message.api.headers.RValue;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;
import io.rtcore.sip.message.uri.SipUri;
import io.rtcore.sip.message.uri.Uri;
import io.rtcore.sip.message.uri.UriVisitor;

/**
 * Representation of a SIP request.
 *
 *
 *
 */
public interface SipRequest extends SipMessage {

  SipMethod method();

  Uri uri();

  Optional<UnsignedInteger> maxForwards();

  Optional<TokenSet> proxyRequire();

  List<Authorization> proxyAuthorization();

  List<Authorization> authorization();

  Optional<UnsignedInteger> expires();

  List<NameAddr> path();

  Optional<EventSpec> event();

  Optional<CharSequence> userAgent();

  Optional<Replaces> replaces();

  Optional<NameAddr> referTo();

  Optional<NameAddr> referredBy();

  Optional<TokenSet> requestDisposition();

  Optional<TokenSet> privacy();

  List<RValue> resourcePriority();

  Optional<NameAddr> pServedUser();

  Optional<RAck> rack();

  List<NameAddr> pAssertedIdentity();

  Optional<Reason> reason();

  Optional<TargetDialog> targetDialog();

  /**
   *
   * @param headers
   * @return
   */

  SipMessage withReplacedHeaders(final RawHeader... headers);

  SipRequest withUri(final Uri Uri);

  SipRequest withMethod(final SipMethod cancel);

  SipRequest withBody(final byte[] body);

  SipRequest withBody(final String body);

  @Override
  SipRequest withoutHeaders(final String... headerNames);

  @Override
  SipRequest withoutHeaders(final SipHeaderDefinition<?>... headers);

  @Override
  <T> SipRequest withReplacedHeader(final SipHeaderDefinition<T> header, final T value);

  /**
   * Prepends a field to a collection header (e.g, one which is List<T>).
   *
   * @param name
   *          The header name
   * @param field
   *          The header field value, in parsed form.
   *
   * @return
   */
  @Override
  SipRequest withPrepended(final String name, final Object field);

  @Override
  SipRequest withAppended(final String name, final Object field);

  <T> SipRequest withParsed(final String name, final List<T> fields);

  SipRequest withPrepended(final RawHeader raw);

  /**
   * Each of the P-Asseted-Identity values in the SIP-Request.
   *
   * If there is no headers, an empty immutable list will be returned.
   *
   * @return
   */

  @Override
  SipRequest withFrom(final NameAddr na);

  @Override
  SipRequest withTo(final NameAddr na);

  SipRequest withTo(final UnaryOperator<NameAddr> na);

  SipRequest withFrom(final UnaryOperator<NameAddr> na);

  SipRequest withRoute(final List<NameAddr> routeSet);

  SipRequest withContact(final NameAddr local);

  SipRequest withContact(final Uri local);

  @Override
  SipRequest withCSeq(final long seqNum, final SipMethod method);

  @Override
  SipRequest withIncrementedCSeq(final SipMethod method);

  @Override
  SipRequest withCallId(String callId);

  @Override
  default String asString() {
    return RfcSerializerManager.defaultSerializer().serialize(this);
  }

  default <T> T uri(final UriVisitor<T> visitor) {
    return this.uri().apply(visitor);
  }

  default OptionalInt expiresSeconds() {
    final Optional<UnsignedInteger> expires = this.expires();
    return expires.map(e -> OptionalInt.of(e.intValue())).orElse(OptionalInt.empty());
  }

  SipRequest withMaxForwards(int i);

  SipRequest withPrependedRecordRoute(NameAddr na);

  /**
   * adds a Record-Route, which likely should include a 'lr' parameter. generally, they shuold
   * always be SIP uris.
   */

  default SipRequest withPrependedRecordRoute(final SipUri address) {
    return this.withPrependedRecordRoute(NameAddr.of(address));
  }

  @Override
  default SipMethodId methodId() {
    return this.method().methodId();
  }

}
