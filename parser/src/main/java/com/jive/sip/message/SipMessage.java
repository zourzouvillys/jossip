package com.jive.sip.message;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.api.BranchId;
import com.jive.sip.message.api.CSeq;
import com.jive.sip.message.api.ContactSet;
import com.jive.sip.message.api.ContentDisposition;
import com.jive.sip.message.api.MinSE;
import com.jive.sip.message.api.NameAddr;
import com.jive.sip.message.api.SessionExpires;
import com.jive.sip.message.api.SipHeaderDefinition;
import com.jive.sip.message.api.SipMessageVisitor;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.message.api.TokenSet;
import com.jive.sip.message.api.Via;
import com.jive.sip.message.api.headers.CallId;
import com.jive.sip.message.api.headers.HistoryInfo;
import com.jive.sip.message.api.headers.MIMEType;
import com.jive.sip.message.api.headers.ParameterizedUri;
import com.jive.sip.uri.Uri;

import lombok.NonNull;

/**
 * A SIP message.
 *
 * Note that there is no guarantee that a message is valid. You must run the SipMessage through a
 * validator if you wish to ensure it's valid.
 *
 * @author theo
 *
 */

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */

public interface SipMessage extends Serializable {

  public static final String VERSION = "SIP/2.0";

  /**
   * Fetches the given value from the message, which is probably a SIP header.
   *
   * @param header
   *          The typed accessor to use to access the field.
   *
   * @return The field, wrapped in an optional value.
   *
   */

  public <T> Optional<T> getHeader(final SipHeaderDefinition<T> header);

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

  List<RawHeader> getHeaders(final String... names);

  /**
   * Returns a single RawHeader value if one exists with this name (case insensitive). If more than
   * one exists, the first is returned.
   */

  Optional<RawHeader> getHeader(final String name);

  /**
   * Returns the history info header. An empty history info is provided if there are no values.
   */

  @NonNull
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

  public void validate();

  /**
   * Fetches the top via header branch parameter value, if it exists. otherwise null.
   *
   * @return
   */

  public BranchId branchId();

  public SipMessage withHeader(final RawHeader header);

  public <T> SipMessage withReplacedHeader(final SipHeaderDefinition<T> header, final T value);

  public SipMessage withoutHeaders(final String... headerNames);

  public SipMessage withoutHeaders(final SipHeaderDefinition... headers);

  public SipMessage withBody(final String contentType, final byte[] body);

  public Optional<String> contentType();

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

}
