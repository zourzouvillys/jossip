package io.rtcore.sip.channels.netty.udp;

import java.net.InetSocketAddress;

import io.rtcore.sip.channels.api.SipRequestFrame;

public record SipDatagramRequest(SipRequestFrame req, InetSocketAddress recipient, InetSocketAddress sender) {
}
