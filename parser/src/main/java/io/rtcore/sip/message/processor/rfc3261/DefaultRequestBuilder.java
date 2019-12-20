package io.rtcore.sip.message.processor.rfc3261;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.api.CSeq;
import io.rtcore.sip.message.message.api.DialogId;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.message.api.RequestBuilder;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.message.message.api.headers.CallId;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManagerBuilder;
import io.rtcore.sip.message.uri.Uri;

public class DefaultRequestBuilder implements RequestBuilder {

  private SipMethod method;
  private NameAddr from;
  private NameAddr to;
  private CallId callID;
  private Uri rUri;
  private CSeq cSeq;
  private String fromTag;
  private String toTag;
  private String body;
  private UnsignedInteger maxForwards = UnsignedInteger.valueOf(70); // Default Max-Forwards to 70
  private Via via;
  private final RfcSipMessageManager manager;
  private final Map<String, Object> extraHeaders = Maps.newHashMap();
  private final Map<SipMethod, CSeq> cseqs = Maps.newHashMap();
  private Collection<NameAddr> route;

  public DefaultRequestBuilder() {
    this.manager = (RfcSipMessageManager) new RfcSipMessageManagerBuilder().build();
  }

  public DefaultRequestBuilder(final RfcSipMessageManager manager) {
    this.manager = manager;
  }

  @Override
  public RequestBuilder setMethod(final SipMethod method) {
    this.method = method;
    return this;
  }

  @Override
  public RequestBuilder setRequestUri(final Uri uri) {
    this.rUri = uri;
    return this;
  }

  @Override
  public RequestBuilder setFrom(final NameAddr from) {
    this.from = from;
    return this;
  }

  @Override
  public RequestBuilder setTo(final NameAddr to) {
    this.to = to;
    return this;
  }

  @Override
  public RequestBuilder setFromTag(final String tag) {
    this.fromTag = tag;
    return this;
  }

  @Override
  public RequestBuilder setToTag(final String tag) {
    this.toTag = tag;
    return this;
  }

  @Override
  public RequestBuilder setCallID(final CallId callID) {
    this.callID = callID;
    return this;
  }

  @Override
  public RequestBuilder setCSeq(final CSeq cSeq) {
    this.cSeq = cSeq;
    return this;
  }

  @Override
  public RequestBuilder setMaxForwards(final int max) {
    this.maxForwards = UnsignedInteger.valueOf(max);
    return this;
  }

  @Override
  public RequestBuilder setVia(final Via via) {
    this.via = via;
    return this;
  }

  @Override
  public RequestBuilder setDialogId(final DialogId dialog) {
    this.callID = dialog.callId();
    this.fromTag = dialog.remoteTag();
    this.toTag = dialog.localTag();
    return this;
  }

  @Override
  public RequestBuilder setHeader(final String name, final Object value) {
    if ("CSeq".equalsIgnoreCase(name)) {
      this.setCSeq((CSeq) value);
    }
    else if ("Max-Forwards".equalsIgnoreCase(name)) {
      this.setMaxForwards((Integer) value);
    }
    else if ("Via".equalsIgnoreCase(name) || "v".equals(name)) {
      this.setVia((Via) value);
    }
    else {
      this.extraHeaders.put(name, value);
    }
    return this;
  }

  @Override
  public RequestBuilder convertFromResponse(final SipResponse response) {
    this.callID = response.callId();
    this.from = response.from();
    this.to = response.to();

    if (response.contacts().isPresent() && (response.contacts().get().size() > 0)) {
      // should never generate a request with R-URI that doesn't come fron the contact.
      this.rUri = response.contacts().get().iterator().next().address();
    }
    else {
      this.rUri = response.toAddress();
    }
    return this;
  }

  @Override
  public RequestBuilder setBody(final String body) {
    this.body = body;
    this.extraHeaders.put("Content-Length", body.length());
    return this;
  }

  private void validate() {
    String errorMessage = null;

    if (this.method == null) {
      errorMessage = "Request method cannot be null.";
    }
    else if (this.rUri == null) {
      errorMessage = "Request URI cannot be null.";
    }
    else if (this.from == null) {
      errorMessage = "Request 'From' NameAddr cannot be null.";
    }
    else if (this.to == null) {
      errorMessage = "Request 'To' NameAddr cannot be null.";
    }
    else if (this.callID == null) {
      errorMessage = "Request Call-ID cannot be null.";
    }
    if (errorMessage != null) {
      throw new IllegalArgumentException(errorMessage);
    }
  }

  @Override
  public SipRequest build() {
    this.validate();

    if (this.fromTag != null) {
      this.from = this.from.withParameter(Token.from("tag"), Token.from(this.fromTag));
    }

    if (this.toTag != null) {
      this.to = this.to.withParameter(Token.from("tag"), Token.from(this.toTag));
    }

    final RfcSerializerManager serializer = new RfcSerializerManagerBuilder().build();
    final DefaultSipRequest req = new DefaultSipRequest(this.manager, this.method, this.rUri);

    // Add required headers
    req.addHeader("From", serializer.serialize(this.from), this.from);
    req.addHeader("To", serializer.serialize(this.to), this.to);
    req.addHeader("Call-ID", serializer.serialize(this.callID), this.callID);

    // Handle other defaults
    req.addHeader("Max-Forwards", this.maxForwards.toString(), this.maxForwards);

    // CSeq automagic management
    if (this.cSeq == null) {
      this.cSeq = Optional.ofNullable(this.cseqs.get(this.method)).orElse(new CSeq(0, this.method)).withNextSequence();
    }
    this.cseqs.put(this.method, this.cSeq);
    req.addHeader("CSeq", serializer.serialize(this.cSeq), this.cSeq);
    this.cSeq = null;

    // Via management

    if (this.via != null) {
      req.addHeader(new RawHeader("Via", serializer.serialize(this.via)));
    }

    // route headers
    if (this.route != null) {
      for (final NameAddr r : this.route) {
        req.addHeader(new RawHeader("Route", serializer.serialize(r)));
      }
    }

    // Add extra headers
    for (final Entry<String, Object> header : this.extraHeaders.entrySet()) {
      req.addHeader(header.getKey(), serializer.serialize(header.getValue()), header.getValue());
    }

    if (this.body != null) {
      req.setBody(this.body.getBytes(StandardCharsets.UTF_8));
    }

    return req;
  }

  public void setRoute(final Collection<NameAddr> route) {
    this.route = route;
  }

}
