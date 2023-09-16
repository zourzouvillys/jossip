package io.rtcore.sip.channels.netty.udp;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rtcore.sip.channels.netty.ClientBranchId;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.frame.SipFrame;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.frame.SipResponseFrame;
import io.rtcore.sip.message.message.api.CSeq;
import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.CSeqParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.ViaParser;
import io.rtcore.sip.netty.codec.udp.SipDatagramPacket;

public class ClientBranchListener {

  private static final Logger logger = LoggerFactory.getLogger(ClientBranchListener.class);

  /**
   * the exchanges currently in progress.
   */

  private ConcurrentMap<ClientBranchId, SipDatagramClientExchange> clientExchanges = new ConcurrentHashMap<>();
  private Consumer<SipDatagramRequest> receiver;

  public ClientBranchListener(Consumer<SipDatagramRequest> receiver) {
    this.receiver = receiver;
  }

  /**
   * find the exchange, if there is one.
   */

  Optional<SipDatagramClientExchange> exchange(ClientBranchId branchId) {
    return Optional.ofNullable(this.clientExchanges.get(branchId));
  }

  /**
   * 
   * @param branchId
   * @return
   */

  Runnable register(ClientBranchId branchId, SipDatagramClientExchange exchange) {
    if (this.clientExchanges.putIfAbsent(branchId, exchange) != null) {
      // attempted to put a branch which is already active.
      throw new IllegalStateException();
    }
    return () -> clientExchanges.remove(branchId, exchange);
  }

  void accept(SipDatagramPacket pkt) {

    SipFrame frame = pkt.content();

    if (frame instanceof SipRequestFrame req) {

      // this is an incoming request.
      this.receiver.accept(new SipDatagramRequest(req, pkt.recipient(), pkt.sender()));

    }
    else if (frame instanceof SipResponseFrame res) {

      Via topVia =
        res
          .headerLines()
          .stream()
          .filter(hdr -> hdr.knownHeaderId().orElse(null) == StandardSipHeaders.VIA)
          .map(SipHeaderLine::headerValues)
          .findFirst()
          .map(ViaParser.INSTANCE::parseFirstValue)
          .orElse(null);

      if (topVia == null) {
        logger.warn("response without valid Via header: {}", res);
        return;
      }

      if (!topVia.protocol().transport().equals("UDP")) {
        logger.warn("dropping response with invalid protocol transport: {}", topVia.protocol());
        return;
      }

      String branchId = topVia.branchWithoutCookie().orElse(null);

      CSeq cseq =
        res
          .headerLines()
          .stream()
          .filter(hdr -> hdr.knownHeaderId().orElse(null) == StandardSipHeaders.CSEQ)
          .map(SipHeaderLine::headerValues)
          .findFirst()
          .map(CSeqParser.INSTANCE::parseFirstValue)
          .orElse(null);

      if (cseq == null) {
        logger.warn("response without valid CSeq header: {}", res);
        return;
      }

      ClientBranchId clientKey = new ClientBranchId(topVia.sentBy(), cseq.methodId(), branchId);

      SipDatagramClientExchange exchange = this.clientExchanges.get(clientKey);

      if (exchange == null) {
        logger.warn("response for unknown sip exchange: {}", clientKey);
        return;
      }

      exchange.accept(res, pkt.sender());

    }
    else {
      throw new IllegalArgumentException();
    }

  }

}
