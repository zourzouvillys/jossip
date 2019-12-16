package com.jive.sip.processor.rfc3261;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.base.api.Token;
import com.jive.sip.message.api.CSeq;
import com.jive.sip.message.api.ContactSet;
import com.jive.sip.message.api.ContentDisposition;
import com.jive.sip.message.api.MinSE;
import com.jive.sip.message.api.NameAddr;
import com.jive.sip.message.api.SessionExpires;
import com.jive.sip.message.api.SessionExpires.Refresher;
import com.jive.sip.message.api.SipMessage;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.message.api.TokenSet;
import com.jive.sip.message.api.Via;
import com.jive.sip.message.api.headers.CallId;
import com.jive.sip.message.api.headers.HistoryInfo;
import com.jive.sip.message.api.headers.MIMEType;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManagerBuilder;
import com.jive.sip.uri.api.SipUri;
import com.jive.sip.uri.api.Uri;

/**
 * Base class for building SIP requests and responses.
 *
 * @author theo
 *
 * @param <T>
 */

@SuppressWarnings("unchecked")
public abstract class MutableSipMessage<T extends MutableSipMessage<T>>
{

  public static final SipMessageManager MM = new RfcSipMessageManagerBuilder().build();
  public static final RfcSerializerManager SM = new RfcSerializerManagerBuilder().build();

  private NameAddr to;
  private NameAddr from;
  private CSeq cseq;
  private ContactSet contact;

  private String callId;
  private List<NameAddr> route;
  private List<NameAddr> recordRoute;

  private String contentType;
  private byte[] body;
  private List<Via> vias;

  private Long expires = null;
  private String sessionId = null;

  private TokenSet allow = null;
  private TokenSet supported = null;
  private TokenSet require = null;
  private TokenSet allowEvents = null;
  private Collection<MIMEType> accept = null;

  private HistoryInfo historyInfo = null;

  private List<RawHeader> headers = null;
  private ContentDisposition contentDisposition;
  private SessionExpires sessionExpires;
  private MinSE minse;

  protected static final RfcSerializerManager serializer = new RfcSerializerManagerBuilder().build();

  public abstract SipMessage build(final SipMessageManager manager);

  public T allow(final TokenSet tokens)
  {
    this.allow = tokens;
    return (T) this;
  }

  public T allow(final Collection<SipMethod> methods)
  {
    return this.allow(TokenSet.fromList(methods.stream().map(m -> m.toString())
        .collect(Collectors.toList())));
  }

  public T supported(final TokenSet tokens)
  {
    this.supported = tokens;
    return (T) this;
  }

  public T require(final TokenSet tokens)
  {
    this.require = tokens;
    return (T) this;
  }

  public T allowEvents(final TokenSet tokens)
  {
    this.allowEvents = tokens;
    return (T) this;
  }

  public T accept(final Collection<MIMEType> value)
  {
    this.accept = value;
    return (T) this;
  }

  public T allow(final SipMethod... methods)
  {
    return this.allow(TokenSet.fromList(Lists.newArrayList(methods).stream().map(e -> e.toString())
        .collect(Collectors.toList())));
  }

  public T supported(final String... token)
  {
    return this.supported(TokenSet.fromList(Arrays.stream(token).collect(Collectors.toList())));
  }

  public T require(final String... token)
  {
    return this.require(TokenSet.fromList(Arrays.stream(token).collect(Collectors.toList())));
  }

  public T allowEvents(final String... token)
  {
    return this.allowEvents(TokenSet.fromList(Arrays.stream(token).collect(Collectors.toList())));
  }

  public T accept(final MIMEType... token)
  {
    return this.accept(Lists.newArrayList(token));
  }

  public T via(final Via via)
  {
    this.vias = Lists.newArrayList(via);
    return (T) this;
  }

  public T via(final Collection<Via> vias)
  {
    this.vias = Lists.newArrayList(vias);
    return (T) this;
  }

  public T to(final Uri uri)
  {
    return this.to(new NameAddr(uri));
  }

  public T to(final Uri uri, final String tag)
  {
    NameAddr na = new NameAddr(uri);
    if (!Strings.isNullOrEmpty(tag))
    {
      na = na.withParameter(Token.from("tag"), Token.from(tag));
    }
    return this.to(na);
  }

  public T to(final NameAddr na)
  {
    this.to = na;
    return (T) this;
  }

  public T from(final Uri uri)
  {
    return this.from(uri, null, null);
  }

  public T toTag(final String tag)
  {
    this.to = this.to.withParameter(Token.from("tag"), Token.from(tag));
    return (T) this;
  }

  public T from(final NameAddr from)
  {
    this.from = from;
    return (T) this;
  }

  public T from(final NameAddr name, final String tag)
  {
    return this.from(name.getAddress(), name.getName().orElse(null), tag);
  }

  public T from(final Uri uri, final String tag)
  {
    return this.from(uri, null, tag);
  }

  public T from(final Uri uri, final String name, final String tag)
  {
    NameAddr na;
    if (Strings.isNullOrEmpty(name))
    {
      na = new NameAddr(uri);
    }
    else
    {
      na = new NameAddr(name, uri);
    }
    if (!Strings.isNullOrEmpty(tag))
    {
      na = na.withParameter(Token.from("tag"), Token.from(tag));
    }
    return this.from(na);
  }

  public T callId(final String callId)
  {
    this.callId = callId;
    return (T) this;
  }

  public T contact(final Uri uri)
  {
    this.contact = ContactSet.singleValue(uri);
    return (T) this;
  }

  public T contact(final NameAddr na)
  {
    this.contact = ContactSet.from(Lists.newArrayList(na));
    return (T) this;
  }

  public T contacts(final NameAddr[] targets)
  {
    this.contact = ContactSet.from(Lists.newArrayList(targets));
    return (T) this;
  }
  
  public T contacts(Iterable<NameAddr> values) {
    this.contact = ContactSet.from(ImmutableList.copyOf(values));
    return (T) this;
  }



  public T contacts(final Uri[] targets)
  {

    final List<NameAddr> nas = Lists.newArrayListWithCapacity(targets.length);

    for (final Uri uri : targets)
    {
      nas.add(new NameAddr(uri));
    }

    this.contact = ContactSet.from(nas);

    return (T) this;

  }

  public T historyInfo(final HistoryInfo hi)
  {
    this.historyInfo = hi;
    return (T) this;
  }

  public T body(final String type, final String body)
  {
    return this.body(type, body.getBytes(StandardCharsets.UTF_8));
  }

  public T body(final String type, final String body, final ContentDisposition disposition)
  {
    return this.body(type, body.getBytes(StandardCharsets.UTF_8), disposition);
  }

  public T body(final String type, final byte[] body)
  {
    this.contentType = type;
    this.body = body.clone();
    return (T) this;
  }

  public T body(final String type, final byte[] body, final ContentDisposition disposition)
  {
    this.contentType = type;
    this.body = body.clone();
    this.contentDisposition = disposition;
    return (T) this;
  }

  public T route(final List<NameAddr> routeSet)
  {
    if ((routeSet != null) && (routeSet.isEmpty() == false))
    {
      this.route = Lists.newArrayList(routeSet);
    }
    return (T) this;
  }

  public T recordRoute(final List<NameAddr> recordRoute)
  {
    if ((recordRoute != null) && !recordRoute.isEmpty())
    {
      this.recordRoute = Lists.newArrayList(recordRoute);
    }
    return (T) this;
  }

  public T prependLooseRecordRoute(final HostAndPort host)
  {

    if (this.recordRoute == null)
    {
      this.recordRoute = Lists.newLinkedList();
    }

    this.recordRoute.add(0, new NameAddr(SipUri.create(host).withParameter(Token.from("lr"))));

    return (T) this;
  }

  public T session(final String session)
  {
    if (session != null)
    {
      this.sessionId = session;
    }
    return (T) this;
  }

  public T route(final NameAddr route)
  {
    return this.route(Lists.newArrayList(route));
  }

  public T route(final Uri route)
  {
    return this.route(Lists.newArrayList(new NameAddr(route)));
  }

  public T cseq(final long seq, final SipMethod method)
  {
    this.cseq = new CSeq(seq, method);
    return (T) this;
  }

  public T cseq(final UnsignedInteger seq, final SipMethod method)
  {
    this.cseq = new CSeq(seq, method);
    return (T) this;
  }

  public T expires(final long expires)
  {
    this.expires = expires;
    return (T) this;
  }

  public T sessionExpires(final long seconds, final Refresher refresher)
  {
    this.sessionExpires = new SessionExpires(seconds, refresher);
    return (T) this;
  }

  public T sessionExpires(final long seconds)
  {
    this.sessionExpires = new SessionExpires(seconds);
    return (T) this;
  }

  public T minse(final int seconds)
  {
    this.minse = new MinSE(java.time.Duration.ofSeconds(seconds));
    return (T) this;
  }

  /**
   * TODO: can we not create an object formed message here and serialize that?
   *
   * @return
   */

  public List<RawHeader> toRawHeaders()
  {

    final List<RawHeader> headers = Lists.newLinkedList();

    if (this.headers != null)
    {
      headers.addAll(this.headers);
    }

    if (this.vias != null)
    {
      for (final Via v : this.vias)
      {
        headers.add(new RawHeader("Via", MutableSipMessage.serializer.serialize(v)));
      }
    }

    if (this.to != null)
    {
      headers.add(new RawHeader("To", MutableSipMessage.serializer.serialize(this.to)));
    }

    if (this.from != null)
    {
      headers.add(new RawHeader("From", MutableSipMessage.serializer.serialize(this.from)));
    }

    if (this.callId != null)
    {
      headers.add(new RawHeader("Call-ID", MutableSipMessage.serializer.serialize(this.callId)));
    }

    if (this.contact != null)
    {
      for (final NameAddr na : this.contact)
      {
        headers.add(new RawHeader("Contact", MutableSipMessage.serializer.serialize(na)));
      }
    }

    if (this.cseq != null)
    {
      headers.add(new RawHeader("CSeq", MutableSipMessage.serializer.serialize(this.cseq)));
    }

    if (this.route != null)
    {
      for (final NameAddr r : this.route)
      {
        headers.add(new RawHeader("Route", MutableSipMessage.serializer.serialize(r)));
      }
    }

    if (this.recordRoute != null)
    {
      for (final NameAddr rr : this.recordRoute)
      {
        headers.add(new RawHeader("Record-Route", MutableSipMessage.serializer.serialize(rr)));
      }
    }

    if (this.body != null)
    {

      headers.add(new RawHeader("Content-Type", this.contentType));
      headers.add(new RawHeader("Content-Length", Long.toString(this.body.length)));

      if (this.contentDisposition != null)
      {
        headers.add(new RawHeader("Content-Disposition", MutableSipMessage.serializer
            .serialize(this.contentDisposition)));
      }

    }
    else
    {
      headers.add(new RawHeader("Content-Length", "0"));
    }

    if (this.expires != null)
    {
      headers.add(new RawHeader("Expires", Long.toString(this.expires)));
    }

    if (this.sessionId != null)
    {
      headers.add(new RawHeader("Session-ID", this.sessionId));
    }

    if (this.historyInfo != null)
    {
      headers.add(new RawHeader("History-Info", MutableSipMessage.serializer.serialize(this.historyInfo)));
    }

    this.add(headers, "Accept", this.accept);
    this.add(headers, "Allow", this.allow);
    this.add(headers, "Supported", this.supported);
    this.add(headers, "Allow-Events", this.allowEvents);
    this.add(headers, "Require", this.require);
    this.add(headers, "Session-Expires", this.sessionExpires);
    this.add(headers, "Min-SE", this.minse);

    return headers;

  }

  private <V> void add(final List<RawHeader> headers, final String name, final V value)
  {

    if (value != null)
    {
      headers.add(new RawHeader(name, MutableSipMessage.serializer.serialize(value)));
    }

  }

  public byte[] getBody()
  {
    return this.body;
  }

  public T add(final String name, final String value)
  {
    if (this.headers == null)
    {
      this.headers = Lists.newLinkedList();
    }
    this.headers.add(new RawHeader(name, value));
    return (T) this;
  }

  /**
   * sets the session SDP.
   */

  public T sdp(final String sdp)
  {
    return this.body("application/sdp", sdp);
  }

  public T callId(final CallId callId)
  {
    return this.callId(callId.getValue());
  }

  @Override
  public String toString()
  {
    final StringWriter sw = new StringWriter(1024);
    try
    {
      SM.serialize(sw, this.build(MM));
    }
    catch (final IOException e)
    {
      throw new RuntimeException(e);
    }
    return sw.toString();
  }

  public abstract SipMessage build();

}
