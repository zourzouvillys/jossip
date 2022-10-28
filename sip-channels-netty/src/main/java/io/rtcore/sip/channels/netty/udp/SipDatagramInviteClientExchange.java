package io.rtcore.sip.channels.netty.udp;

import java.net.InetSocketAddress;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterators;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.UnicastProcessor;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipClientExchange;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.api.SipResponseFrame;
import io.rtcore.sip.channels.netty.ClientBranchId;

class SipDatagramClientExchange implements SipClientExchange {

  private static final Logger logger = LoggerFactory.getLogger(SipDatagramClientExchange.class);

  //
  private final SipRequestFrame req;

  // completes sucesfully once no more responses can be received (from point of view of the
  // transaction user). note that this is different than the state remaining present, as we keep
  // additional state.
  private UnicastProcessor<Event> responses = UnicastProcessor.create(true);

  // handle to remove ourselves from the listener.
  private Runnable listenerHandle;
  private Runnable transmitHandle;

  /**
   * @param req
   * @param branchId
   */

  SipDatagramClientExchange(NettyUdpSocket socket, InetSocketAddress recipient, SipRequestFrame req, ClientBranchId branchId) {

    this.req = req;

    // create the client transaction key, which we will use for listening for responses.
    this.listenerHandle = socket.listener().register(branchId, this);

    //
    
    //
    this.transmitHandle =
      socket.transmit(recipient,
        req,
        Iterators.forArray(
          Duration.ofMillis(500),
          Duration.ofSeconds(1),
          Duration.ofSeconds(2),
          Duration.ofSeconds(4),
          Duration.ofSeconds(4),
          Duration.ofSeconds(4),
          Duration.ofSeconds(4),
          Duration.ofSeconds(4),
          Duration.ofSeconds(4)));

  }

  @Override
  public SipRequestFrame request() {
    return this.req;
  }

  @Override
  public SipAttributes attributes() {
    return SipAttributes.of();
  }

  @Override
  public Flowable<Event> responses() {
    return this.responses;
  }

  @Override
  public boolean cancel() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipDatagramClientExchange.cancel invoked.");
  }

  /**
   * called with an incoming sip packet.
   */

  void accept(SipResponseFrame res, InetSocketAddress sender) {

    logger.debug("got response from {}: {}", sender, res);

    this.responses.onNext(new Event(null, res));

    if (res.initialLine().code() >= 200) {
      this.responses.onComplete();
      this.transmitHandle.run();
      this.listenerHandle.run();
    }

    // time out?

  }

}
