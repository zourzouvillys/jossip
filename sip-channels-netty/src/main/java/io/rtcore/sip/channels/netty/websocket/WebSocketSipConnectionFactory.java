package io.rtcore.sip.channels.netty.websocket;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.rtcore.sip.channels.api.SipAttributes;

@FunctionalInterface
public interface WebSocketSipConnectionFactory {

  WebSocketSipConnection createConnection(WebSocketServerHandshaker handshaker, Channel ch, SipAttributes attributes);

}
