package io.rtcore.gateway.client.proxy;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.rxjava3.core.Maybe;
import io.rtcore.gateway.client.SipGateway;
import io.rtcore.gateway.client.SipGateway.ClientInviteDelegate;
import io.rtcore.sip.common.SipHeaderLine;

public class OutgoingSipExchange implements ProxyBranchTarget {

  private static final Logger log = LoggerFactory.getLogger(OutgoingSipExchange.class);

  private final SipGateway gateway;
  private final URI ruri;
  private final List<SipHeaderLine> headers;
  private final Optional<String> body;

  private OutgoingSipExchange(final SipGateway gateway, final URI ruri, final Collection<SipHeaderLine> headers, final Optional<String> body) {
    this.gateway = gateway;
    this.ruri = ruri;
    this.headers = List.copyOf(headers);
    this.body = body;
  }

  @Override
  public void send(final ClientInviteDelegate delegate, final Maybe<ProxyCancelReason> cancellationToken) {
    log.info("sending request to uri [{}], headers {}, body {}", this.ruri, this.headers, this.body.map(e -> Integer.toString(e.length())).orElse("0"));
    this.gateway.INVITE(this.ruri, this.headers, this.body, delegate);
  }

  public static OutgoingSipExchange create(final SipGateway gateway, final URI ruri, final Collection<SipHeaderLine> headers, final Optional<String> body) {
    return new OutgoingSipExchange(gateway, ruri, headers, body);
  }

}
