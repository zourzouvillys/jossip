package com.jive.sip.processor.rfc3261;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.auth.headers.Authorization;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.SipRequest;
import com.jive.sip.message.SipResponse;
import com.jive.sip.message.api.CSeq;
import com.jive.sip.message.api.EventSpec;
import com.jive.sip.message.api.NameAddr;
import com.jive.sip.message.api.RAck;
import com.jive.sip.message.api.Reason;
import com.jive.sip.message.api.Replaces;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.message.api.SubscriptionState;
import com.jive.sip.message.api.Via;
import com.jive.sip.message.api.ViaProtocol;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.TokenParameterValue;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.uri.Uri;

public class MutableSipRequest extends MutableSipMessage<MutableSipRequest> {

  private final Uri ruri;
  private final SipMethod method;
  private NameAddr referTo = null;
  private NameAddr referredBy;
  private SubscriptionState subscriptionState;
  private EventSpec event;
  private UnsignedInteger expires;
  private Reason reason;
  private UnsignedInteger mf;
  private Replaces replaces;
  private String userAgent;
  private RAck rack;
  private List<Authorization> proxyAuthorization;

  public MutableSipRequest replaces(final Replaces replaces) {
    this.replaces = replaces;
    return this;
  }

  public MutableSipRequest(final SipMethod method, final Uri ruri) {
    this.method = method;
    this.ruri = ruri;
  }

  public MutableSipRequest maxForwards(final int value) {
    this.mf = UnsignedInteger.valueOf(value);
    return this;
  }

  public MutableSipRequest referTo(final Uri referTo) {
    this.referTo(new NameAddr(referTo));
    return this;
  }

  public MutableSipRequest expires(final int expires) {
    this.expires = UnsignedInteger.valueOf(expires);
    return this;
  }

  public MutableSipRequest referTo(final NameAddr na) {
    this.referTo = na;
    return this;
  }

  public MutableSipRequest referredBy(final NameAddr nameAddr) {
    this.referredBy = nameAddr;
    return this;
  }

  public MutableSipRequest subscriptionState(final SubscriptionState subscriptionState) {
    this.subscriptionState = subscriptionState;
    return this;
  }

  public MutableSipRequest event(final String eventType) {
    this.event = new EventSpec(eventType);
    return this;
  }

  public MutableSipRequest event(final String eventType, final String id) {
    this.event = new EventSpec(eventType, id);
    return this;
  }

  public void reason(final Reason reason) {
    this.reason = reason;
  }

  public MutableSipRequest proxyAuthorization(final List<Authorization> creds) {
    this.proxyAuthorization = creds;
    return this;
  }

  /**
   * Creates an unmutable {@link SipRequest} from this instance.
   *
   * @return
   */

  @Override
  public SipRequest build(final SipMessageManager manager) {

    final List<RawHeader> headers = super.toRawHeaders();

    if (this.referTo != null) {
      headers.add(new RawHeader("Refer-To", MutableSipMessage.serializer.serialize(this.referTo)));
      if (this.referredBy != null) {
        headers.add(new RawHeader("Referred-By", this.referredBy.address().toString()));
      }
    }

    if (this.event != null) {
      headers.add(new RawHeader("Event", MutableSipMessage.serializer.serialize(this.event)));
    }

    if (this.subscriptionState != null) {
      headers.add(new RawHeader("Subscription-State", this.subscriptionState.toString()));
    }

    if (this.expires != null) {
      headers.add(new RawHeader("Expires", this.expires.toString()));
    }

    if (this.reason != null) {
      headers.add(new RawHeader("Reason", MutableSipMessage.serializer.serialize(this.reason)));
    }

    if (this.replaces != null) {
      headers.add(new RawHeader("Replaces", MutableSipMessage.serializer.serialize(this.replaces)));
    }

    if (this.mf != null) {
      headers.add(new RawHeader("Max-Forwards", this.mf.toString()));
    }

    if (this.userAgent != null) {
      headers.add(new RawHeader("User-Agent", this.userAgent));
    }

    if (this.rack != null) {
      headers.add(new RawHeader("RAck", MutableSipMessage.serializer.serialize(this.rack)));
    }

    if (this.proxyAuthorization != null) {
      for (final Authorization auth : this.proxyAuthorization) {
        headers.add(new RawHeader("Proxy-Authorization", MutableSipMessage.serializer.serialize(auth)));
      }
    }

    return manager.createRequest(this.method, this.ruri, headers, super.getBody());

  }

  public static MutableSipRequest fromRequest(final SipRequest req) {

    final MutableSipRequest m = new MutableSipRequest(req.method(), req.uri());

    return m;

  }

  /**
   * Constructs a new SIP INVITE.
   *
   * @param ruri
   *
   * @return
   */

  public static MutableSipRequest createInvite(final Uri ruri) {
    final MutableSipRequest req = new MutableSipRequest(SipMethod.INVITE, ruri);
    return req;
  }

  public static MutableSipRequest create(final SipMethod method, final Uri ruri) {
    final MutableSipRequest req = new MutableSipRequest(method, ruri);
    return req;
  }

  public MutableSipRequest userAgent(final String userAgent) {
    this.userAgent = userAgent;
    return this;
  }

  public MutableSipRequest rack(final UnsignedInteger rseq, final CSeq cseq) {
    this.rack = new RAck(rseq, cseq);
    return this;
  }

  public MutableSipRequest rack(final long rseq, final CSeq cseq) {
    return rack(UnsignedInteger.valueOf(rseq), cseq);
  }

  public MutableSipRequest cseq(final CSeq cseq) {
    return this.cseq(cseq.sequence(), cseq.method());
  }

  public static MutableSipRequest ack(final SipResponse res200) {
    final MutableSipRequest ack = MutableSipRequest.create(SipMethod.ACK, res200.contacts().get().iterator().next().address());
    ack.cseq(res200.cseq().sequence(), SipMethod.ACK);
    ack.callId(res200.callId());
    ack.to(res200.to());
    ack.from(res200.from());
    return ack;
  }

  public MutableSipRequest via(final ViaProtocol proto, final HostAndPort sentby, final String branch, final boolean rport) {
    final ArrayList<RawParameter> rparams = Lists.newArrayList();

    rparams.add(new RawParameter("branch", new TokenParameterValue(String.format("z9hG4bK%s", branch))));

    if (rport) {
      rparams.add(new RawParameter("rport"));
    }
    return this.via(new Via(proto, sentby, DefaultParameters.from(rparams)));
  }

  /**
   * Create an ACK for responding to a failure. (hop by hop).
   *
   * This does NOT add a Via header. The caller is responsible for ensuring a Via header is added
   * before being sent on the wire.
   *
   * @param req
   * @param res
   * @return
   */

  public static MutableSipRequest createFailureAck(final SipRequest req, final SipResponse res) {
    return MutableSipRequest.create(SipMethod.ACK, req.uri())
      .callId(req.callId().getValue())
      .cseq(req.cseq().sequence(), SipMethod.ACK)
      .from(req.from())
      .to(res.to())
      .session(res.sessionId().orElse(null))
      .route(req.route());
  }

  @Override
  public SipRequest build() {
    return this.build(MM);
  }

}
