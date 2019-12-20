package io.rtcore.sip.message.message.api;

import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.api.headers.CallId;
import io.rtcore.sip.message.uri.Uri;

public interface RequestBuilder {
  public RequestBuilder setMethod(final SipMethod method);

  public RequestBuilder setRequestUri(final Uri uri);

  public RequestBuilder setDialogId(final DialogId dialog);

  public RequestBuilder setFrom(final NameAddr from);

  public RequestBuilder setFromTag(final String tag);

  public RequestBuilder setTo(final NameAddr to);

  public RequestBuilder setToTag(final String tag);

  public RequestBuilder setCallID(final CallId callID);

  public RequestBuilder setCSeq(final CSeq cSeq);

  public RequestBuilder setMaxForwards(final int max);

  public RequestBuilder setVia(final Via via);

  public RequestBuilder setHeader(final String name, final Object value);

  public RequestBuilder setBody(final String body);

  public SipRequest build();

  RequestBuilder convertFromResponse(final SipResponse response);
}
