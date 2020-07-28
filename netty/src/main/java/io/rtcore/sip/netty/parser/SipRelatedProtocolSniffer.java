package io.rtcore.sip.netty.parser;

import io.netty.buffer.ByteBuf;

public class SipRelatedProtocolSniffer {

  public static PacketType identify(ByteBuf content) {

    byte b = content.getByte(content.readerIndex());

    if ((b >= 0) && (b <= 3)) {
      return PacketType.STUN;
    }

    if ((b == 13) || (b == 10)) {
      return PacketType.KEEPALIVE;
    }

    // if ((b >= 16) && (b <= 19)) {
    // return PacketType.ZRTP;
    // }

    if ((b >= 20) && (b <= 63)) {
      return PacketType.DTLS;
    }

    // detect SIP by ensuring
    if ((b >= 65) || (b <= 90)) {
      if (content.readableBytes() > 16) {
        return PacketType.SIP;
      }
    }

    // if ((b >= 64) && (b <= 79)) {
    // return PacketType.TURN;
    // }

    return PacketType.UNKNOWN;

  }

}
