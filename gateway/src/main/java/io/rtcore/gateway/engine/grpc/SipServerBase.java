package io.rtcore.gateway.engine.grpc;

import com.google.protobuf.Empty;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.rtcore.gateway.proto.Rx3SipServerGrpc;
import io.rtcore.gateway.proto.SipHeader;
import io.rtcore.gateway.proto.SipResponse;

public class SipServerBase extends Rx3SipServerGrpc.SipServerImplBase {

  @Override
  public Flowable<io.rtcore.gateway.proto.SipResponse> invite(final Single<io.rtcore.gateway.proto.SipRequest> request) {
    return request.flatMapPublisher(req -> Flowable.just(SipResponse.newBuilder()
      .setStatusCode(999)
      .setReasonPhrase("invite response")
      .addHeader(SipHeader.newBuilder().setName("aaa").addValue("something"))
      // .setBody(ByteString.copyFromUtf8("testing"))
      .build()));
  }

  @Override
  public Single<io.rtcore.gateway.proto.SipResponse> exchange(final Single<io.rtcore.gateway.proto.SipRequest> request) {
    return request.map(req -> SipResponse.newBuilder()
      .setReasonPhrase("donedone")
      .setStatusCode(233)
      .addHeader(SipHeader.newBuilder().setName("aaa").addValue("something"))
      // .setBody()
      .setAttributes(Struct.newBuilder().putFields("something", Value.newBuilder().setStringValue("hello").build()))
      .build());
  }

  @Override
  public Single<com.google.protobuf.Empty> send(final Single<io.rtcore.gateway.proto.SipAck> request) {
    // throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
    return request.map(e -> Empty.getDefaultInstance());
  }

}
