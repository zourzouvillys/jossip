package io.rtcore.sip.message.message;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.content.SipContent;
import io.rtcore.sip.message.message.api.BranchId;
import io.rtcore.sip.message.message.api.CSeq;
import io.rtcore.sip.message.message.api.ContactSet;
import io.rtcore.sip.message.message.api.ContentDisposition;
import io.rtcore.sip.message.message.api.MinSE;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.message.api.SessionExpires;
import io.rtcore.sip.message.message.api.SipHeaderDefinition;
import io.rtcore.sip.message.message.api.SipMessageVisitor;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.message.api.TokenSet;
import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.message.message.api.headers.CallId;
import io.rtcore.sip.message.message.api.headers.HistoryInfo;
import io.rtcore.sip.message.message.api.headers.MIMEType;
import io.rtcore.sip.message.message.api.headers.ParameterizedUri;
import io.rtcore.sip.message.uri.Uri;

/**
 * A SIP message.
 *
 * Note that there is no guarantee that a message is valid. You must run the SipMessage through a
 * validator if you wish to ensure it's valid.
 */

public interface SipMessage extends Serializable {

  String VERSION = "SIP/2.0";

  /**
   * Fetches the given value from the message, which is probably a SIP header.
   *
   * @param header
   *          The typed accessor to use to access the field.
   *
   * @return The field, wrapped in an optional value.
   *
   */

  <T> Optional<T> getHeader(final SipHeaderDefinition<T> header);

  /**
   * Returns the headers as raw keys and values.
   *
   * @return an immutable collection of raw headers.
   */

  List<RawHeader> headers();

  /**
   * The content of this message.
   *
   * TODO: TPZ: this is leaky, as the caller could modify it. Fix.
   *
   * @return the content, as a byte array.
   */

  byte[] body();

  /**
   * The parsed Via headers.
   */

  List<Via> vias();

  /**
   * @return URIHeader
   */

  NameAddr to();

  /**
   * @return URIHeader
   */

  NameAddr from();

  /*
   * ===== Common SIP Values ===== There are here as helpers.
   */

  /**
   * The call identifier.
   *
   * @return The call-id, or null if one is not present in the message.
   */

  CallId callId();

  /**
   * The value of the 'tag' parameter in the 'From' header,
   *
   * @return The value or null if the From header doesn't exist or doesn't contain a 'tag'
   *         parameter.
   */

  String fromTag();

  /**
   * The value of the 'tag' parameter in the 'To' header,
   *
   * @return The value or null if the To header doesn't exist or doesn't contain a 'tag' parameter.
   */

  String toTag();

  /**
   * The value of the address section of the From header.
   *
   * @return String
   */

  default Uri fromAddress() {
    return from().address();
  }

  /**
   * The value of the address section of the To header.
   *
   * @return String
   */

  Uri toAddress();

  /**
   * The value(s) for the Contact header(s) or Optional.absent.
   *
   * @return
   */

  Optional<ContactSet> contacts();

  /**
   * The parsed CSeq header.
   *
   * @return
   */

  CSeq cseq();

  /**
   * The parsed Route headers.
   *
   * @return
   */

  List<NameAddr> route();

  /**
   * The parsed Record-Route headers.
   *
   * @return
   */

  List<NameAddr> recordRoute();

  /**
   *
   */

  Optional<SessionExpires> sessionExpires();

  /**
   *
   * @return
   */

  Optional<String> sessionId();

  /**
   * Returns a {@link RawHeader} instance for the value of the header with the given name.
   *
   * Note this will NOT return the compact header form. You will need to pass any names for a header
   * in to retrieve the values.
   *
   * This method will also not fold any headers, etc.
   *
   * @param name
   * @return
   */

  List<RawHeader> getHeaders(final Collection<String> names);

  /**
   * Returns a single RawHeader value if one exists with this name (case insensitive). If more than
   * one exists, the first is returned.
   */

  Optional<RawHeader> getHeader(final String name);

  /**
   * Returns the history info header. An empty history info is provided if there are no values.
   */

  HistoryInfo historyInfo();

  /**
   * Visitors which can be used to walk all parts of the message.
   *
   * @param visitor
   * @throws IOException
   */

  void accept(final SipMessageVisitor visitor) throws IOException;

  /**
   * The SIP version, e.g "SIP/2.0".
   */

  String version();

  /**
   *
   * @return
   */

  Optional<List<MIMEType>> accept();

  /**
   *
   * @return
   */

  Optional<TokenSet> require();

  /**
   *
   * @return
   */

  Optional<TokenSet> supported();

  /**
   *
   */

  List<ParameterizedUri> alertInfo();

  /**
   * Runs through each header and ensures it is valid.
   */

  void validate();

  /**
   * Fetches the top via header branch parameter value, if it exists. otherwise null.
   *
   * @return
   */

  BranchId branchId();

  SipMessage withHeader(final RawHeader header);

  <T> SipMessage withReplacedHeader(final SipHeaderDefinition<T> header, final T value);

  SipMessage withoutHeaders(final String... headerNames);

  SipMessage withoutHeaders(final SipHeaderDefinition... headers);

  SipMessage withBody(final MIMEType contentType, final byte[] body);

  Optional<MIMEType> contentType();

  Optional<TokenSet> allow();

  /**
   * @param name
   * @param field
   * @return return a copy of this object with a new header prepended to the list.
   */
  SipMessage withPrepended(final String name, final Object field);

  SipMessage withAppended(final String name, final Object field);

  SipMessage withFrom(final NameAddr na);

  SipMessage withTo(final NameAddr na);

  SipMessage withCSeq(final long seqNum, final SipMethod method);

  SipMessage withIncrementedCSeq(final SipMethod method);

  Optional<ContentDisposition> contentDisposition();

  Optional<MinSE> minSE();

  default Optional<Via> topVia() {
    if (vias().isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(vias().get(0));
  }

  default <T> T apply(Function<SipMessage, T> applicator) {
    return applicator.apply(this);
  }

  /**
   * fetch body. note it may be a reference or some other content, rather than actual binary
   * payload.
   */

  Optional<SipContent> body(String disposition);

  default void accept(Consumer<SipRequest> req, Consumer<SipResponse> res) {
    if (this instanceof SipRequest) {
      req.accept((SipRequest) this);
    }
    else if (this instanceof SipResponse) {
      res.accept((SipResponse) this);
    }
    else {
      throw new IllegalArgumentException();
    }
  }

  default <R> R apply(Function<SipRequest, R> req, Function<SipResponse, R> res) {
    if (this instanceof SipRequest) {
      return req.apply((SipRequest) this);
    }
    else if (this instanceof SipResponse) {
      return res.apply((SipResponse) this);
    }
    else {
      throw new IllegalArgumentException();
    }
  }

}
