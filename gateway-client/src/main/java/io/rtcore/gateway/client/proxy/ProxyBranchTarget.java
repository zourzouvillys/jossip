package io.rtcore.gateway.client.proxy;

import io.reactivex.rxjava3.core.Maybe;
import io.rtcore.gateway.client.SipGateway.ClientInviteDelegate;

public interface ProxyBranchTarget {

  void send(ClientInviteDelegate delegate, Maybe<ProxyCancelReason> cancellationToken);

}
