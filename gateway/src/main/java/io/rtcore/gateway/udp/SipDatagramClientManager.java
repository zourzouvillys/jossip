package io.rtcore.gateway.udp;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.rng.core.source64.MersenneTwister64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.subjects.SingleSubject;
import io.rtcore.gateway.engine.sip.ClientBranchId;
import io.rtcore.gateway.engine.sip.NetworkSegment;
import io.rtcore.sip.common.HostPort;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.frame.SipResponseFrame;
import io.rtcore.sip.message.message.api.BranchId;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.message.message.api.ViaProtocol;
import io.rtcore.sip.message.uri.SipUri;
import io.rtcore.sip.netty.codec.SipParsingUtils;
import io.rtcore.sip.netty.codec.SipParsingUtils.TopViaRemovalResult;

/**
 * manages SIP datagram based on client transactions.
 */

public class SipDatagramClientManager {

    private static final Logger LOG = LoggerFactory.getLogger(SipDatagramClientManager.class);
    private final MersenneTwister64 rng = new MersenneTwister64(new long[] { ThreadLocalRandom.current().nextLong() });

    private NetworkSegment segment;
    private SipDatagramSocket udp;

    public SipDatagramClientManager(NetworkSegment segment, SipDatagramSocket udp) {
        this.segment = segment;
        this.udp = udp;
    }

    public Flowable<SipResponseFrame> transmit(SipRequestFrame request, InetSocketAddress recipient) {
        try {
            switch (request.initialLine().method().token()) {
                case "ACK":
                    // ACK only transmits, and never receives responses.
                    return Completable.fromCompletionStage(this.udp.transmit(recipient, request)).toFlowable();
                case "INVITE":
                    return transmitInvite(request, recipient);
                default:
                    return transmitNonInvite(request, recipient);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Flowable.error(e);
        }
    }

    private Flowable<SipResponseFrame> transmitInvite(SipRequestFrame request, InetSocketAddress recipient) {
        LOG.info("transmitting INVITE to {}: {}", recipient, request);
        return Completable.fromCompletionStage(this.udp.transmit(recipient, request)).toFlowable();
    }

    private Flowable<SipResponseFrame> transmitNonInvite(SipRequestFrame request, InetSocketAddress recipient) {

        LOG.info("transmitting {} to {}: {}", request.initialLine().method(), recipient, request);

        //
        List<SipHeaderLine> headers = new ArrayList<>(request.headerLines());

        // add our Max-Forwards header if missing.
        if (Iterables.isEmpty(StandardSipHeaders.MAX_FORWARDS.all(headers))) {
            headers.add(StandardSipHeaders.MAX_FORWARDS.ofLine("70"));
        }

        // add our From header if missing.
        if (Iterables.isEmpty(StandardSipHeaders.FROM.all(headers))) {
            headers.add(StandardSipHeaders.FROM.ofLine(NameAddr.of(SipUri.ANONYMOUS)
                    .withTag(generateLocalTag())
                    .encode()));
        }

        // add our To header if missing.
        if (Iterables.isEmpty(StandardSipHeaders.TO.all(headers))) {
            headers.add(StandardSipHeaders.TO.ofLine("<" + request.initialLine().uri().toASCIIString() + ">"));
        }

        // add CSeq header if missing.
        if (Iterables.isEmpty(StandardSipHeaders.CSEQ.all(headers))) {
            headers.add(StandardSipHeaders.CSEQ.ofLine("1 " + request.initialLine().method().token()));
        }

        // add Call-ID header if missing.
        if (Iterables.isEmpty(StandardSipHeaders.CALL_ID.all(headers))) {
            headers.add(StandardSipHeaders.CALL_ID.ofLine(generateCallId()));
        }

        // --- Via header handling.

        // create a new transaction branch, 8 random characters.
        String branchId = generateBranchId();

        // add the Via header
        Via topVia = Via.of(
                ViaProtocol.UDP,
                HostPort.fromHost("invalid"),
                BranchId.withCookiePrepended(branchId))
                .withRPort();

        // add our Via at the top.
        headers.add(0, StandardSipHeaders.VIA.ofLine(topVia.encode()));

        ClientBranchId transactionId = new ClientBranchId(
                topVia.sentBy(),
                request.initialLine().method(),
                branchId);

        LOG.info("transactionId: {}", transactionId);

        SingleSubject<SipResponseFrame> responses = SingleSubject.create();

        // handle is called when the transaction is complete.
        Runnable handle = segment.allocateClientTransaction(transactionId, request.initialLine().method(),
                (response) -> {
                    if (response.initialLine().code() < 200) {
                        // ignore provisional responses.
                        LOG.info("ignoring provisional response for NICT: {}", response.initialLine());
                        return;
                    }
                    // remove the top Via header field.
                    // we can't just remove the whole line, as it may contain
                    // other field values that just ours.
                    TopViaRemovalResult modified = SipParsingUtils.removeTopViaHeader(response.headerLines());

                    if (modified.topVia().isEmpty()) {
                        LOG.error("no Via header found in response: {}", response);
                        return;
                    }

                    LOG.info("received response: {}, {}", response.initialLine(), modified.topVia());

                    // replace with the modified headers that doesn't include the first Via field
                    response = response.withHeaderLines(modified.headers());

                    // notify listener.
                    responses.onSuccess(response);

                });

        // calculate all the headers we need to add.
        request = request.withHeaderLines(headers);

        // we first send the request, and then stream all the responses.

        // TODO: retransmit until we get a response or timeout.
        // TODO: start absorbing rather than removing completely.

        return Completable.fromCompletionStage(this.udp.transmit(recipient, request))
                .andThen(responses)
                .doOnError(err -> err.printStackTrace())
                .doOnTerminate(handle::run)
                .toFlowable();

    }

    // return 8 random alphanumeric characters.
    private String generateBranchId() {
        return String.format("%016x", rng.nextLong());
    }

    // return 8 random alphanumeric characters.
    private String generateCallId() {
        return String.format("%016x", rng.nextLong());
    }

    // return 8 random alphanumeric characters.
    private String generateLocalTag() {
        return String.format("%08x", rng.nextInt());
    }

}
