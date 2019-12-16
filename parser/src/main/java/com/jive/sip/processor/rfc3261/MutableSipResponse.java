package com.jive.sip.processor.rfc3261;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.auth.headers.Authorization;
import com.jive.sip.auth.headers.DigestCredentials;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.message.api.SipResponseStatus;
import com.jive.sip.message.api.TokenSet;
import com.jive.sip.message.api.headers.Warning;
import com.jive.sip.parameters.api.QuotedStringParameterValue;

public class MutableSipResponse extends MutableSipMessage<MutableSipResponse> {

  private static final QuotedStringParameterValue AUTH = new QuotedStringParameterValue("auth");

  private SipResponseStatus status;
  private Authorization wwwAuthenticate;
  private Authorization proxyAuthenticate;
  private TokenSet unsupported;
  private Duration minExpires;
  private String server;
  private Integer retryAfterSeconds;
  private UnsignedInteger rseq;
  private List<Warning> warns;

  public MutableSipResponse(final SipResponseStatus status) {
    this.status = status;
  }

  public MutableSipResponse status(final SipResponseStatus status) {
    this.status = status;
    return this;
  }

  public MutableSipResponse retryAfter(final int retryAfterSeconds) {
    Preconditions.checkArgument(retryAfterSeconds >= 0);
    this.retryAfterSeconds = retryAfterSeconds;
    return this;
  }

  public MutableSipResponse wwwAuthenticate(final Authorization auth) {
    this.wwwAuthenticate = auth;
    return this;
  }

  public MutableSipResponse proxyAuthenticate(final Authorization auth) {
    this.proxyAuthenticate = auth;
    return this;
  }

  public SipResponse toResponse() {
    return this.build(MM);
  }

  /**
   * Creates an unmutable {@link SipRequest} from this instance.
   *
   * @return
   */

  @Override
  public SipResponse build(final SipMessageManager manager) {
    final List<RawHeader> headers = super.toRawHeaders();

    if (this.wwwAuthenticate != null) {
      headers.add(new RawHeader("WWW-Authenticate", MutableSipMessage.serializer.serialize(this.wwwAuthenticate)));
    }

    if (this.proxyAuthenticate != null) {
      headers.add(new RawHeader("Proxy-Authenticate", MutableSipMessage.serializer.serialize(this.proxyAuthenticate)));
    }

    if (this.unsupported != null) {
      headers.add(new RawHeader("Unsupported", MutableSipMessage.serializer.serialize(this.unsupported)));
    }

    if (this.minExpires != null) {
      headers.add(new RawHeader("Min-Expires", Long.toString(this.minExpires.getSeconds())));
    }

    if (this.server != null) {
      headers.add(new RawHeader("Server", this.server));
    }

    if (this.retryAfterSeconds != null) {
      headers.add(new RawHeader("Retry-After", Integer.toString(this.retryAfterSeconds)));
    }

    if (this.rseq != null) {
      headers.add(new RawHeader("RSeq", this.rseq.toString()));
    }

    if (this.warns != null) {
      for (final Warning warn : this.warns) {
        headers.add(new RawHeader("Warning", MutableSipMessage.serializer.serialize(warn)));
      }
    }

    return manager.createResponse(this.status, headers, super.getBody());

  }

  public static MutableSipResponse createResponse(final SipRequest req, final SipResponseStatus status) {
    final MutableSipResponse res = new MutableSipResponse(status);

    if ((req.getVias() != null) && !req.getVias().isEmpty()) {
      res.via(req.getVias());
    }

    if (req.getTo() != null) {
      res.to(req.getTo());
    }

    if (req.getFrom() != null) {
      res.from(req.getFrom());
    }

    if (req.getCallId() != null) {
      res.callId(req.getCallId().getValue());
    }

    if (req.getCSeq() != null) {
      res.cseq(req.getCSeq().getSequence().longValue(), req.getCSeq().getMethod());
    }

    if ((req.getRecordRoute() != null) && !req.getRecordRoute().isEmpty()) {
      res.recordRoute(req.getRecordRoute());
    }

    final Optional<RawHeader> session = req.getHeader("Session-ID");
    if (session.isPresent()) {
      res.session(session.get().getValue());
    }

    if (!req.getHistoryInfo().isEmpty()) {
      // todo: check privacy?
      res.historyInfo(req.getHistoryInfo());
    }

    return res;
  }

  public static MutableSipResponse createResponse(final SipResponseStatus status) {
    return new MutableSipResponse(status);
  }

  public MutableSipResponse unsupported(final TokenSet values) {
    this.unsupported = values;
    return this;
  }

  public MutableSipResponse minExpires(final Duration minExpires) {
    this.minExpires = minExpires;
    return this;
  }

  public MutableSipResponse server(final String server) {
    this.server = server;
    return this;
  }

  public MutableSipResponse rseq(final UnsignedInteger value) {
    this.rseq = value;
    return this;
  }

  public MutableSipResponse warning(final List<Warning> warns) {
    this.warns = warns;
    return this;
  }

  public MutableSipResponse rseq(final long i) {
    return this.rseq(UnsignedInteger.valueOf(i));
  }

  public void proxyAuthenticateQopMD5(final String authRealm, final String nonce, final boolean stale) {

    final DigestCredentials creds =
      DigestCredentials.builder()
        .realm(authRealm)
        .nonce(nonce)
        .stale(stale)
        .algorithm(DigestCredentials.MD5)
        .qop("auth")
        .build();

    this.proxyAuthenticate(creds);

  }

  @Override
  public SipResponse build() {
    return this.build(MM);
  }

}
