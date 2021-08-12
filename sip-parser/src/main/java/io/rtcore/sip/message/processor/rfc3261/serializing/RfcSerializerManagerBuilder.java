/**
 *
 */
package io.rtcore.sip.message.processor.rfc3261.serializing;

import java.time.ZonedDateTime;

import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.auth.headers.Authorization;
import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.base.api.RawMessage;
import io.rtcore.sip.message.message.api.CSeq;
import io.rtcore.sip.message.message.api.ContentDisposition;
import io.rtcore.sip.message.message.api.EventSpec;
import io.rtcore.sip.message.message.api.MinSE;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.message.api.RAck;
import io.rtcore.sip.message.message.api.Reason;
import io.rtcore.sip.message.message.api.Replaces;
import io.rtcore.sip.message.message.api.SessionExpires;
import io.rtcore.sip.message.message.api.TokenSet;
import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.message.message.api.headers.CallId;
import io.rtcore.sip.message.message.api.headers.HistoryInfo;
import io.rtcore.sip.message.message.api.headers.MIMEType;
import io.rtcore.sip.message.message.api.headers.ParameterizedString;
import io.rtcore.sip.message.message.api.headers.ParameterizedUri;
import io.rtcore.sip.message.message.api.headers.RetryAfter;
import io.rtcore.sip.message.message.api.headers.RfcTimestamp;
import io.rtcore.sip.message.message.api.headers.Version;
import io.rtcore.sip.message.message.api.headers.Warning;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.processor.rfc3261.DefaultSipRequest;
import io.rtcore.sip.message.processor.rfc3261.DefaultSipResponse;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.AuthorizationSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.CSeqSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.CallIdSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.ContentDispositionSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.DateTimeSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.EventSpecSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.HistoryInfoSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.MIMETypeSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.MinSESerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.NameAddrSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.ParameterizedUriSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.RAckSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.RawHeaderSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.RawMessageSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.RawParameterSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.RawUriSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.ReasonSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.ReplacesSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.RetryAfterSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.RfcTimestampSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.SessionExpiresSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.SipRequestSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.SipResponseSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.SipUriSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.TelUriSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.TokenSetSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.UnsignedIntegerSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.VersionSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.WarningSerializer;
import io.rtcore.sip.message.processor.uri.RawUri;
import io.rtcore.sip.message.uri.SipUri;
import io.rtcore.sip.message.uri.TelUri;
import io.rtcore.sip.message.uri.UserInfo;

/**
 * 
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
    manager.register(RawMessage.class, new RawMessageSerializer(manager));
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

}
