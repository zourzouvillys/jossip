package io.rtcore.sip.channels.api;

import io.rtcore.sip.channels.connection.SipConnection;

public interface SipServerConnectionDelegate<T extends SipConnection> {

  void onNewConnection(T conn);

}
