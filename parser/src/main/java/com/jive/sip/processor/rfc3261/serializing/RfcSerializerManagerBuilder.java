/**
 *
 */
package com.jive.sip.processor.rfc3261.serializing;

import java.time.ZonedDateTime;

import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.auth.headers.Authorization;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.api.CSeq;
import com.jive.sip.message.api.ContentDisposition;
import com.jive.sip.message.api.EventSpec;
import com.jive.sip.message.api.MinSE;
import com.jive.sip.message.api.NameAddr;
import com.jive.sip.message.api.RAck;
import com.jive.sip.message.api.Reason;
import com.jive.sip.message.api.Replaces;
import com.jive.sip.message.api.SessionExpires;
import com.jive.sip.message.api.TokenSet;
import com.jive.sip.message.api.Via;
import com.jive.sip.message.api.headers.CallId;
import com.jive.sip.message.api.headers.HistoryInfo;
import com.jive.sip.message.api.headers.MIMEType;
import com.jive.sip.message.api.headers.ParameterizedString;
import com.jive.sip.message.api.headers.ParameterizedUri;
import com.jive.sip.message.api.headers.RetryAfter;
import com.jive.sip.message.api.headers.RfcTimestamp;
import com.jive.sip.message.api.headers.Version;
import com.jive.sip.message.api.headers.Warning;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.processor.rfc3261.DefaultSipRequest;
import com.jive.sip.processor.rfc3261.DefaultSipResponse;
import com.jive.sip.processor.rfc3261.serializing.serializers.AuthorizationSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.CSeqSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.CallIdSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.ContentDispositionSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.DateTimeSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.EventSpecSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.HistoryInfoSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.MIMETypeSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.MinSESerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.NameAddrSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.ParameterizedUriSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.RAckSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.RawHeaderSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.RawParameterSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.RawUriSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.ReasonSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.ReplacesSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.RetryAfterSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.RfcTimestampSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.SessionExpiresSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.SipRequestSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.SipResponseSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.SipUriSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.TelUriSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.TokenSetSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.UnsignedIntegerSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.VersionSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.WarningSerializer;
import com.jive.sip.processor.uri.RawUri;
import com.jive.sip.uri.api.SipUri;
import com.jive.sip.uri.api.TelUri;
import com.jive.sip.uri.api.UserInfo;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */
public class RfcSerializerManagerBuilder {

  public RfcSerializerManager build() {
    final RfcSerializerManager manager = new RfcSerializerManager();
    this.register(manager);
    return manager;
  }

  private void register(final RfcSerializerManager manager) {
    manager.register(DefaultSipRequest.class, new SipRequestSerializer(manager));
    manager.register(DefaultSipResponse.class, new SipResponseSerializer(manager));
    manager.register(Authorization.class, new AuthorizationSerializer(manager));
    manager.register(CallId.class, new CallIdSerializer());
    manager.register(CSeq.class, new CSeqSerializer());
    manager.register(ContentDisposition.class, new ContentDispositionSerializer(manager));
    manager.register(ZonedDateTime.class, new DateTimeSerializer());
    manager.register(EventSpec.class, new EventSpecSerializer(manager));
    manager.register(HistoryInfo.class, new HistoryInfoSerializer(manager));
    manager.register(MIMEType.class, new MIMETypeSerializer(manager));
    manager.register(MinSE.class, new MinSESerializer(manager));
    manager.register(NameAddr.class, new NameAddrSerializer(manager));
    manager.register(ParameterizedString.class, new ParamaterizedStringSerializer(manager));
    manager.register(ParameterizedUri.class, new ParameterizedUriSerializer(manager));
    manager.register(RawHeader.class, new RawHeaderSerializer());
    manager.register(RawParameter.class, new RawParameterSerializer());
    manager.register(RawUri.class, new RawUriSerializer());
    manager.register(Reason.class, new ReasonSerializer(manager));
    manager.register(Replaces.class, new ReplacesSerializer(manager));
    manager.register(RetryAfter.class, new RetryAfterSerializer(manager));
    manager.register(RfcTimestamp.class, new RfcTimestampSerializer());
    manager.register(RAck.class, new RAckSerializer());
    manager.register(SessionExpires.class, new SessionExpiresSerializer(manager));
    manager.register(SipUri.class, new SipUriSerializer(manager));
    manager.register(TelUri.class, new TelUriSerializer(manager));
    manager.register(TokenSet.class, new TokenSetSerializer());
    manager.register(UserInfo.class, new UserInfoSerializer());
    manager.register(Version.class, new VersionSerializer());
    manager.register(Via.class, new ViaSerializer(manager));
    manager.register(Warning.class, new WarningSerializer());
    manager.register(UnsignedInteger.class, new UnsignedIntegerSerializer());
  }

  public static void main(final String[] args) {
    new RfcSerializerManagerBuilder().build();
  }
}
