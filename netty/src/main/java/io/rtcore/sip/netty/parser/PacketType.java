package io.rtcore.sip.netty.parser;

public enum PacketType {
  SIP,
  STUN,
  // TURN,
  KEEPALIVE,
  DTLS,
  // RTP,
  // RTCP,
  UNKNOWN
}
