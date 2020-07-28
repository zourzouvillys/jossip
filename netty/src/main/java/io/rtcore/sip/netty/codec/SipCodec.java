package io.rtcore.sip.netty.codec;


import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * it would be awesome if we could use the netty HTTP codecs. However, we need to support both
 * request and response on both sides. unlike HTTP, SIP doesn't have a client/server role on a
 * connection once it's established - and we see both on both sides.
 * 
 * @author theo
 *
 */

public class SipCodec extends CombinedChannelDuplexHandler<SipObjectDecoder, SipResponseEncoder> {

  public SipCodec() {
    this(4096, 1024 * 64, 1024 * 64);
  }

  /**
   * Creates a new instance with the specified decoder options.
   */

  public SipCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
    init(new SipObjectDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, false),
      new SipServerResponseEncoder());
  }

  /**
   * Creates a new instance with the specified decoder options.
   */
  public SipCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
    init(
      new SipObjectDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders),
      new SipServerResponseEncoder());
  }

  /**
   * Creates a new instance with the specified decoder options.
   */
  public SipCodec(
      int maxInitialLineLength,
      int maxHeaderSize,
      int maxChunkSize,
      boolean validateHeaders,
      int initialBufferSize) {
    init(
      new SipObjectDecoder(
        maxInitialLineLength,
        maxHeaderSize,
        maxChunkSize,
        false,
        validateHeaders,
        initialBufferSize),
      new SipServerResponseEncoder());
  }

  private final class SipServerResponseEncoder extends SipResponseEncoder {

  }


}
