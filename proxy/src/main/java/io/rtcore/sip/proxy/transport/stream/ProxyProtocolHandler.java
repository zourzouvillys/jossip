package io.rtcore.sip.proxy.transport.stream;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;

import com.google.common.net.InetAddresses;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;

public class ProxyProtocolHandler extends ByteToMessageDecoder {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProxyProtocolHandler.class);
  private InetSocketAddress external;

  public ProxyProtocolHandler(InetSocketAddress external) {
    this.setSingleDecode(true);
    this.external = external;
  }

  // the magic V2 signature.
  private static final byte[] SIG =
    new byte[] {

      0x0D,
      0x0A,
      0x0D,
      0x0A,

      0x00,
      0x0D,
      0x0A,
      0x51,

      0x55,
      0x49,
      0x54,
      0x0A

    };

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

    // @formatter:off
    //    struct proxy_hdr_v2 {
    //      uint8_t sig[12];  /* hex 0D 0A 0D 0A 00 0D 0A 51 55 49 54 0A */
    //      uint8_t ver_cmd;  /* protocol version and command */
    //      uint8_t fam;      /* protocol family and address */
    //      uint16_t len;     /* number of following bytes part of the header */
    //  };
    // @formatter:on

    if (!in.isReadable(16)) {
      log.debug("need more data (16)");
      return;
    }

    for (int i = 0; i < SIG.length; ++i) {
      if (in.getByte(in.readerIndex() + i) != SIG[i]) {
        log.debug("IN:\n\n{}\n", ByteBufUtil.prettyHexDump(in));
        throw new DecoderException("input does not contain proxy v2 signature");
      }
    }

    int len = in.getUnsignedShort(in.readerIndex() + SIG.length + 2);

    if (!in.isReadable(16 + len)) {
      // need more data.
      log.debug("need more data (16 + {})", len);
      return;
    }

    int ver = in.getUnsignedByte(in.readerIndex() + SIG.length) & 0xF0;
    int cmd = in.getUnsignedByte(in.readerIndex() + SIG.length) & 0x0F;
    int fam = in.getUnsignedByte(in.readerIndex() + SIG.length + 1);

    if (ver != 0x20) {
      throw new DecoderException("unsupported proxy v2 ver_cmd: 0x" + Integer.toHexString(ver));
    }

    //
    log.debug("ver: {}, cmd: {}, family {}, len {}", ver, cmd, fam, len);

    // actually commit now, skipping over the static stuffs.
    in.skipBytes(16);

    //

    ByteBuf buf = in.readSlice(len);

    switch (cmd) {
      case 0x00: // LOCAL command
        // ignore the header.
        break;
      case 0x01: // PROXY command
        try {
          // System.err.println(ByteBufUtil.prettyHexDump(buf));
          InetSocketAddress[] vals = decode(fam, len, buf);
          ctx.fireUserEventTriggered(new ProxyProtocolCompletionEvent(vals[0], vals[1]));
        }
        catch (Exception ex) {
          log.warn("proxy protocol error: {}", ex.getMessage(), ex);
        }
        break;
      default:
        throw new DecoderException("unsupported proxy v2 cmd: 0x" + Integer.toHexString(cmd));
    }

    ctx.channel().pipeline().remove(this);

    //

    // If the length specified in the PROXY protocol header indicates that additional
    // bytes are part of the header beyond the address information, a receiver may
    // choose to skip over and ignore those bytes, or attempt to interpret those
    // bytes.
    //
    // The information in those bytes will be arranged in Type-Length-Value (TLV
    // vectors) in the following format. The first byte is the Type of the vector.
    // The second two bytes represent the length in bytes of the value (not included
    // the Type and Length bytes), and following the length field is the number of
    // bytes specified by the length.
    //
    // struct pp2_tlv {
    // uint8_t type;
    // uint8_t length_hi;
    // uint8_t length_lo;
    // uint8_t value[0];
    // };
    //
    // A receiver may choose to skip over and ignore the TLVs he is not interested in
    // or he does not understand. Senders can generate the TLVs only for
    // the information they choose to publish.
    //
    // The following types have already been registered for the <type> field :
    //
    // #define PP2_TYPE_ALPN 0x01
    // #define PP2_TYPE_AUTHORITY 0x02
    // #define PP2_TYPE_CRC32C 0x03
    // #define PP2_TYPE_NOOP 0x04
    // #define PP2_TYPE_SSL 0x20
    // #define PP2_SUBTYPE_SSL_VERSION 0x21
    // #define PP2_SUBTYPE_SSL_CN 0x22
    // #define PP2_SUBTYPE_SSL_CIPHER 0x23
    // #define PP2_SUBTYPE_SSL_SIG_ALG 0x24
    // #define PP2_SUBTYPE_SSL_KEY_ALG 0x25
    // #define PP2_TYPE_NETNS 0x30

  }

  private InetSocketAddress[] decode(int fam, int len, ByteBuf in) {
    switch (fam) {
      case 0x11: {
        if (!in.isReadable(12)) {
          throw new DecoderException("invalid length for IPv4 proxy protocol family");
        }
        // v4
        Inet4Address srcAddr = InetAddresses.fromInteger(in.readInt());
        Inet4Address dstAddr = InetAddresses.fromInteger(in.readInt());
        int srcPort = in.readUnsignedShort();
        int dstPort = in.readUnsignedShort();
        return new InetSocketAddress[] {
          new InetSocketAddress(srcAddr, srcPort),
          external, // new InetSocketAddress(dstAddr, dstPort),
        };
      }
      case 0x21: {
        if (!in.isReadable(36)) {
          throw new DecoderException("invalid length for IPv6 proxy protocol family");
        }
        try {
          // v6
          byte[] buf = new byte[16];
          in.readBytes(buf);
          InetAddress srcAddr = Inet6Address.getByAddress(buf);
          in.readBytes(buf);
          InetAddress dstAddr = Inet6Address.getByAddress(buf);
          int srcPort = in.readUnsignedShort();
          int dstPort = in.readUnsignedShort();
          return new InetSocketAddress[] {
            new InetSocketAddress(srcAddr, srcPort),
            new InetSocketAddress(dstAddr, dstPort),
          };
        }
        catch (UnknownHostException ex) {
          throw new RuntimeException(ex);
        }
      }
      default:
        throw new DecoderException("unsupported proxy protocol v2 family: " + Integer.toHexString(fam));
    }

  }

}
