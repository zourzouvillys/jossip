package io.rtcore.sip.channels.netty.udp;

import java.net.InetSocketAddress;

import io.rtcore.sip.frame.SipRequestFrame;

public record SipDatagramRequest(SipRequestFrame req, InetSocketAddress recipient, InetSocketAddress sender) {
}
