package io.rtcore.gateway.engine.grpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;

import io.grpc.Channel;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.rtcore.gateway.proto.Rx3SipServerGrpc;
import io.rtcore.gateway.proto.Rx3SipServerGrpc.RxSipServerStub;
import io.rtcore.gateway.proto.SipBody;
import io.rtcore.gateway.proto.SipExchangeReply;
import io.rtcore.gateway.proto.SipExchangeRequest;
import io.rtcore.gateway.proto.SipHeader;
import io.rtcore.gateway.proto.SipRequest;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.SipInitialLine.RequestLine;
import io.rtcore.sip.frame.SipRequestFrame;

public class SipGrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(SipGrpcClient.class);
    private final RxSipServerStub stub;

    private SipGrpcClient(Channel channel) {
        this.stub = Rx3SipServerGrpc.newRxStub(channel);
    }

    public Flowable<SipExchangeReply> exchange(SipRequestFrame request, String connectionId) {
        SipExchangeRequest req = SipExchangeRequest.newBuilder()
                .setFrame(makeRequestFrame(request))
                .setConnectionId(connectionId)
                .build();
        LOG.debug("exchanging SIP request: {}", req);
        return this.stub
                .exchange(Single.just(req))
                .doOnEach(e -> LOG.info("response event", e));
    }

    private SipRequest makeRequestFrame(SipRequestFrame request) {

        SipRequest.Builder b = SipRequest.newBuilder();
        RequestLine i = request.initialLine();

        b.setMethod(i.method().token());
        b.setUri(i.uri().toASCIIString());

        // only set if we discxover a body ...
        request.body().map(ByteString::copyFromUtf8)
                .map(value -> SipBody.newBuilder().setBinary(value))
                .ifPresent(b::setBody);

        // iterate over headerLines
        for (SipHeaderLine h : request.headerLines()) {
            b.addHeader(SipHeader.newBuilder().setName(h.headerName()).addValue(h.headerValues()).build());
        }

        return b.build();

    }

    public static SipGrpcClient create(Channel channel) {
        return new SipGrpcClient(channel);
    }

}
