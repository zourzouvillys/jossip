package io.rtcore.sip.message.processor.rfc3261;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.auth.StdDigestAlgo;
import io.rtcore.sip.message.auth.headers.Authorization;
import io.rtcore.sip.message.auth.headers.DigestCredentials;
import io.rtcore.sip.message.auth.headers.DigestValues;
import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.SipResponseStatus;
import io.rtcore.sip.message.message.api.TokenSet;
import io.rtcore.sip.message.message.api.headers.Warning;

public class MutableSipResponse extends MutableSipMessage<MutableSipResponse> {

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

  public MutableSipResponse wwwAuthenticate(final Consumer<DigestValues.Builder> handler) {
    final DigestValues.Builder b = DigestCredentials.builder();
    handler.accept(b);
    return this.wwwAuthenticate(b.build().asCredentials());
  }

  public MutableSipResponse wwwAuthenticate(final String authRealm, final String nonce, final boolean stale, final String opaque) {
    return this.wwwAuthenticate(b -> b
      .realm(authRealm)
      .nonce(nonce)
      .stale(stale)
      .algorithm(StdDigestAlgo.MD5)
      .qop("auth")
      .opaque(opaque));
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

    if ((req.vias() != null) && !req.vias().isEmpty()) {
      res.via(req.vias());
    }

    if (req.to() != null) {
      res.to(req.to());
    }

    if (req.from() != null) {
      res.from(req.from());
    }

    if (req.callId() != null) {
      res.callId(req.callId().getValue());
    }

    if (req.cseq() != null) {
      res.cseq(req.cseq().sequence().longValue(), req.cseq().method());
    }

    if ((req.recordRoute() != null) && !req.recordRoute().isEmpty()) {
      res.recordRoute(req.recordRoute());
    }

    final Optional<RawHeader> session = req.getHeader("Session-ID");
    if (session.isPresent()) {
      res.session(session.get().value());
    }

    if (!req.historyInfo().isEmpty()) {
      // todo: check privacy?
      res.historyInfo(req.historyInfo());
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

  public MutableSipResponse proxyAuthenticate(final Consumer<DigestValues.Builder> handler) {
    final DigestValues.Builder b = DigestCredentials.builder();
    handler.accept(b);
    return this.proxyAuthenticate(b.build().asCredentials());
  }

  public MutableSipResponse proxyAuthenticateQopAuthMD5(final String authRealm, final String nonce, final boolean stale, final String opaque) {
    return this.proxyAuthenticate(b -> b
      .realm(authRealm)
      .nonce(nonce)
      .stale(stale)
      .algorithm(StdDigestAlgo.MD5)
      .qop("auth")
      .opaque(opaque));
  }

  @Override
  public SipResponse build() {
    return this.build(MM);
  }

}
