package io.rtcore.sip.netty.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.AppendableCharSequence;

public class SipObjectDecoder extends ByteToMessageDecoder {

  private static final String EMPTY_VALUE = "";

  /**
   * The internal state of {@link SipObjectDecoder}. <em>Internal use only</em>.
   */

  private enum State {
    SKIP_CONTROL_CHARS,
    READ_INITIAL,
    READ_HEADER,
    READ_VARIABLE_LENGTH_CONTENT,
    READ_FIXED_LENGTH_CONTENT,
    READ_CHUNK_SIZE,
    READ_CHUNKED_CONTENT,
    READ_CHUNK_DELIMITER,
    READ_CHUNK_FOOTER,
    BAD_MESSAGE,
    UPGRADED
  }

  protected final boolean validateHeaders;
  private final LineParser lineParser;
  private final HeaderParser headerParser;
  private final int maxChunkSize;
  private final boolean chunkedSupported;

  //

  private SipMessage message;
  private long chunkSize;
  private long contentLength = Long.MIN_VALUE;
  private volatile boolean resetRequested;

  // These will be updated by splitHeader(...)
  private CharSequence name;
  private CharSequence value;
  private LastSipContent trailer;

  //
  private State currentState = State.SKIP_CONTROL_CHARS;

  //

  public SipObjectDecoder() {
    this(4096, 8192, 8192, true);
  }

  public SipObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported) {
    this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, true);
  }

  public SipObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported, boolean validateHeaders) {
    this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, validateHeaders, 128);
  }

  public static void verify(boolean expression, String errorMessageTemplate, Object p1) {
    if (!expression) {
      throw new RuntimeException(errorMessageTemplate + ": " + p1);
    }
  }

  public SipObjectDecoder(
      int maxInitialLineLength,
      int maxHeaderSize,
      int maxChunkSize,
      boolean chunkedSupported,
      boolean validateHeaders,
      int initialBufferSize) {

    verify(maxInitialLineLength > 0, "maxInitialLineLength must be a positive integer", maxInitialLineLength);
    verify(maxHeaderSize > 0, "maxHeaderSize must be a positive integer", maxInitialLineLength);
    verify(maxChunkSize > 0, "maxChunkSize must be a positive integer", maxInitialLineLength);

    AppendableCharSequence seq = new AppendableCharSequence(initialBufferSize);

    this.lineParser = new LineParser(seq, maxInitialLineLength);
    this.headerParser = new HeaderParser(seq, maxHeaderSize);
    this.maxChunkSize = maxChunkSize;
    this.chunkedSupported = chunkedSupported;
    this.validateHeaders = validateHeaders;

  }

  protected SipMessage createMessage(String[] initialLine) throws Exception {

    String initial = initialLine[0];

    if (initial.startsWith("SIP/")) {
      return createResponse(initialLine);
    }
    else {
      return createRequest(initialLine);
    }

  }

  protected SipRequest createRequest(String[] initialLine) {
    return new DefaultSipRequest(
      SipVersion.valueOf(initialLine[2]),
      SipMethod.valueOf(initialLine[0]),
      initialLine[1],
      validateHeaders);
  }

  protected SipResponse createResponse(String[] initialLine) {
    return new DefaultSipResponse(
      SipVersion.valueOf(initialLine[0]),
      new SipResponseStatus(Integer.parseInt(initialLine[1]), initialLine[2]));
  }

  protected SipMessage createInvalidMessage() {
    return new InvalidSipMessage();
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {

    if (resetRequested) {
      resetNow();
    }

    switch (currentState) {

      case SKIP_CONTROL_CHARS: {
        if (!skipControlCharacters(buffer)) {
          return;
        }
        currentState = State.READ_INITIAL;
      }

      case READ_INITIAL:

        try {

          AppendableCharSequence line = lineParser.parse(buffer);

          if (line == null) {
            return;
          }

          String[] initialLine = splitInitialLine(line);

          if (initialLine.length < 3) {
            // Invalid initial line - ignore.
            currentState = State.SKIP_CONTROL_CHARS;
            return;
          }

          message = createMessage(initialLine);
          currentState = State.READ_HEADER;

          // fall-through

        }
        catch (Exception e) {
          out.add(invalidMessage(buffer, e));
          return;
        }

      case READ_HEADER:
        try {
          State nextState = readHeaders(buffer);
          if (nextState == null) {
            return;
          }
          currentState = nextState;
          switch (nextState) {
            case SKIP_CONTROL_CHARS:
              // fast-path
              // No content is expected.
              out.add(message);
              out.add(LastSipContent.EMPTY_LAST_CONTENT);
              resetNow();
              return;
            case READ_CHUNK_SIZE:
              if (!chunkedSupported) {
                throw new IllegalArgumentException("Chunked messages not supported");
              }
              // Chunked encoding - generate HttpMessage first. HttpChunks will follow.
              out.add(message);
              return;
            default:

              /**
               * <a href="https://tools.ietf.org/html/rfc7230#section-3.3.3">RFC 7230, 3.3.3</a>
               * states that if a request does not have either a transfer-encoding or a
               * content-length header then the message body length is 0. However for a response the
               * body length is the number of octets received prior to the server closing the
               * connection. So we treat this as variable length chunked encoding.
               */

              long contentLength = contentLength();

              if (contentLength == 0) { // || ((contentLength == -1) && isDecodingRequest())) {
                out.add(message);
                out.add(LastSipContent.EMPTY_LAST_CONTENT);
                resetNow();
                return;
              }

              assert (nextState == State.READ_FIXED_LENGTH_CONTENT)
                ||
                (nextState == State.READ_VARIABLE_LENGTH_CONTENT);

              out.add(message);

              if (nextState == State.READ_FIXED_LENGTH_CONTENT) {
                // chunkSize will be decreased as the READ_FIXED_LENGTH_CONTENT state reads data
                // chunk by chunk.
                chunkSize = contentLength;
              }

              // We return here, this forces decode to be called again where we will decode the
              // content
              return;
          }
        }
        catch (Exception e) {
          out.add(invalidMessage(buffer, e));
          return;
        }
      case READ_VARIABLE_LENGTH_CONTENT: {
        // Keep reading data as a chunk until the end of connection is reached.
        int toRead = Math.min(buffer.readableBytes(), maxChunkSize);
        if (toRead > 0) {
          ByteBuf content = buffer.readRetainedSlice(toRead);
          out.add(new DefaultSipContent(content));
        }
        return;
      }
      case READ_FIXED_LENGTH_CONTENT: {
        int readLimit = buffer.readableBytes();

        // Check if the buffer is readable first as we use the readable byte count
        // to create the HttpChunk. This is needed as otherwise we may end up with
        // create a HttpChunk instance that contains an empty buffer and so is
        // handled like it is the last HttpChunk.
        //
        // See https://github.com/netty/netty/issues/433
        if (readLimit == 0) {
          return;
        }

        int toRead = Math.min(readLimit, maxChunkSize);
        if (toRead > chunkSize) {
          toRead = (int) chunkSize;
        }
        ByteBuf content = buffer.readRetainedSlice(toRead);
        chunkSize -= toRead;

        if (chunkSize == 0) {
          // Read all content.
          out.add(new DefaultLastSipContent(content, validateHeaders));
          resetNow();
        }
        else {
          out.add(new DefaultSipContent(content));
        }
        return;
      }
      /**
       * everything else after this point takes care of reading chunked content. basically, read
       * chunk size, read chunk, read and ignore the CRLF and repeat until 0
       */
      case READ_CHUNK_SIZE:
        try {
          AppendableCharSequence line = lineParser.parse(buffer);
          if (line == null) {
            return;
          }
          int chunkSize = getChunkSize(line.toString());
          this.chunkSize = chunkSize;
          if (chunkSize == 0) {
            currentState = State.READ_CHUNK_FOOTER;
            return;
          }
          currentState = State.READ_CHUNKED_CONTENT;
          // fall-through
        }
        catch (Exception e) {
          out.add(invalidChunk(buffer, e));
          return;
        }
      case READ_CHUNKED_CONTENT: {
        assert chunkSize <= Integer.MAX_VALUE;
        int toRead = Math.min((int) chunkSize, maxChunkSize);
        toRead = Math.min(toRead, buffer.readableBytes());
        if (toRead == 0) {
          return;
        }
        SipContent chunk = new DefaultSipContent(buffer.readRetainedSlice(toRead));
        chunkSize -= toRead;

        out.add(chunk);

        if (chunkSize != 0) {
          return;
        }
        currentState = State.READ_CHUNK_DELIMITER;
        // fall-through
      }
      case READ_CHUNK_DELIMITER: {
        final int wIdx = buffer.writerIndex();
        int rIdx = buffer.readerIndex();
        while (wIdx > rIdx) {
          byte next = buffer.getByte(rIdx++);
          if (next == SipConstants.LF) {
            currentState = State.READ_CHUNK_SIZE;
            break;
          }
        }
        buffer.readerIndex(rIdx);
        return;
      }
      case READ_CHUNK_FOOTER:
        try {
          LastSipContent trailer = readTrailingHeaders(buffer);
          if (trailer == null) {
            return;
          }
          out.add(trailer);
          resetNow();
          return;
        }
        catch (Exception e) {
          out.add(invalidChunk(buffer, e));
          return;
        }
      case BAD_MESSAGE: {
        // Keep discarding until disconnection.
        buffer.skipBytes(buffer.readableBytes());
        break;
      }
      case UPGRADED: {
        int readableBytes = buffer.readableBytes();
        if (readableBytes > 0) {
          // Keep on consuming as otherwise we may trigger an DecoderException,
          // other handler will replace this codec with the upgraded protocol codec to
          // take the traffic over at some point then.
          // See https://github.com/netty/netty/issues/2173
          out.add(buffer.readBytes(readableBytes));
        }
        break;
      }
    }
  }

  @Override
  protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

    super.decodeLast(ctx, in, out);

    if (resetRequested) {
      // If a reset was requested by decodeLast() we need to do it now otherwise we may produce a
      // LastSipContent while there was already one.
      resetNow();
    }
    // Handle the last unfinished message.

    if (message != null) {

      boolean chunked = false; // HttpUtil.isTransferEncodingChunked(message);

      if ((currentState == State.READ_VARIABLE_LENGTH_CONTENT) && !in.isReadable() && !chunked) {
        // End of connection.
        out.add(LastSipContent.EMPTY_LAST_CONTENT);
        resetNow();
        return;
      }

      if (currentState == State.READ_HEADER) {
        // If we are still in the state of reading headers we need to create a new invalid message
        // that
        // signals that the connection was closed before we received the headers.
        out.add(invalidMessage(Unpooled.EMPTY_BUFFER,
          new PrematureChannelClosureException("Connection closed before received headers")));
        resetNow();
        return;
      }

      // Check if the closure of the connection signifies the end of the content.
      boolean prematureClosure;

      // if (isDecodingRequest() || chunked) {
      // // The last request did not wait for a response.
      // prematureClosure = true;
      // }
      // else {
      // Compare the length of the received content and the 'Content-Length' header.
      // If the 'Content-Length' header is absent, the length of the content is determined by the
      // end of the
      // connection, so it is perfectly fine.
      prematureClosure = contentLength() > 0;
      // }

      if (!prematureClosure) {
        out.add(LastSipContent.EMPTY_LAST_CONTENT);
      }
      resetNow();
    }
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    // if (evt instanceof SipExpectationFailedEvent) {
    // switch (currentState) {
    // case READ_FIXED_LENGTH_CONTENT:
    // case READ_VARIABLE_LENGTH_CONTENT:
    // case READ_CHUNK_SIZE:
    // reset();
    // break;
    // default:
    // break;
    // }
    // }
    super.userEventTriggered(ctx, evt);
  }

  protected boolean isContentAlwaysEmpty(SipMessage msg) {
    return false;
  }

  /**
   * Resets the state of the decoder so that it is ready to decode a new message. This method is
   * useful for handling a rejected request with {@code Expect: 100-continue} header.
   */
  public void reset() {
    resetRequested = true;
  }

  private void resetNow() {

    this.message = null;
    name = null;
    value = null;
    contentLength = Long.MIN_VALUE;
    lineParser.reset();
    headerParser.reset();
    trailer = null;

    resetRequested = false;
    currentState = State.SKIP_CONTROL_CHARS;
  }

  private SipMessage invalidMessage(ByteBuf in, Exception cause) {

    currentState = State.BAD_MESSAGE;

    // Advance the readerIndex so that ByteToMessageDecoder does not complain
    // when we produced an invalid message without consuming anything.
    in.skipBytes(in.readableBytes());

    if (message == null) {
      message = createInvalidMessage();
    }

    message.setDecoderResult(DecoderResult.failure(cause));

    SipMessage ret = message;
    message = null;
    return ret;

  }

  private SipContent invalidChunk(ByteBuf in, Exception cause) {
    currentState = State.BAD_MESSAGE;

    // Advance the readerIndex so that ByteToMessageDecoder does not complain
    // when we produced an invalid message without consuming anything.
    in.skipBytes(in.readableBytes());

    SipContent chunk = new DefaultLastSipContent(Unpooled.EMPTY_BUFFER);
    chunk.setDecoderResult(DecoderResult.failure(cause));
    message = null;
    trailer = null;
    return chunk;
  }

  private static boolean skipControlCharacters(ByteBuf buffer) {
    boolean skiped = false;
    final int wIdx = buffer.writerIndex();
    int rIdx = buffer.readerIndex();
    while (wIdx > rIdx) {
      int c = buffer.getUnsignedByte(rIdx++);
      if (!Character.isISOControl(c) && !Character.isWhitespace(c)) {
        rIdx--;
        skiped = true;
        break;
      }
    }
    buffer.readerIndex(rIdx);
    return skiped;
  }

  private State readHeaders(ByteBuf buffer) {

    final SipMessage message = this.message;

    final SipHeaders headers = message.headers();

    AppendableCharSequence line = headerParser.parse(buffer);

    if (line == null) {
      return null;
    }

    if (line.length() > 0) {
      do {
        char firstChar = line.charAt(0);
        if ((name != null) && ((firstChar == ' ') || (firstChar == '\t'))) {
          // please do not make one line from below code
          // as it breaks +XX:OptimizeStringConcat optimization
          String trimmedLine = line.toString().trim();
          String valueStr = String.valueOf(value);
          value = valueStr + ' ' + trimmedLine;

        }
        else {

          if (name != null) {
            headers.add(name, value);
          }
          splitHeader(line);

        }

        line = headerParser.parse(buffer);

        if (line == null) {
          return null;
        }

      }
      while (line.length() > 0);

    }

    // Add the last header.
    if (name != null) {
      headers.add(name, value);
    }
    // reset name and value fields
    name = null;
    value = null;

    State nextState;

    // if (isContentAlwaysEmpty(message)) {
    // HttpUtil.setTransferEncodingChunked(message, false);
    // nextState = State.SKIP_CONTROL_CHARS;
    // }
    // else if (HttpUtil.isTransferEncodingChunked(message)) {
    // nextState = State.READ_CHUNK_SIZE;
    // }
    if (contentLength() >= 0) {
      nextState = State.READ_FIXED_LENGTH_CONTENT;
    }
    else {
      nextState = State.READ_VARIABLE_LENGTH_CONTENT;
    }
    return nextState;
  }

  private long contentLength() {
    if (contentLength == Long.MIN_VALUE) {
      contentLength = SipUtil.getContentLength(message, -1L);
    }
    return contentLength;
  }

  private LastSipContent readTrailingHeaders(ByteBuf buffer) {
    AppendableCharSequence line = headerParser.parse(buffer);
    if (line == null) {
      return null;
    }
    CharSequence lastHeader = null;
    if (line.length() > 0) {
      LastSipContent trailer = this.trailer;
      if (trailer == null) {
        trailer = this.trailer = new DefaultLastSipContent(Unpooled.EMPTY_BUFFER, validateHeaders);
      }
      do {
        char firstChar = line.charAt(0);
        if ((lastHeader != null) && ((firstChar == ' ') || (firstChar == '\t'))) {
          List<CharSequence> current = trailer.trailingHeaders().getAll(lastHeader);
          if (!current.isEmpty()) {
            int lastPos = current.size() - 1;
            // please do not make one line from below code
            // as it breaks +XX:OptimizeStringConcat optimization
            String lineTrimmed = line.toString().trim();
            String currentLastPos = current.get(lastPos).toString();
            current.set(lastPos, currentLastPos + lineTrimmed);
          }
        }
        else {
          splitHeader(line);
          CharSequence headerName = name;
          if (!SipHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(headerName)
            &&
            !SipHeaderNames.TRANSFER_ENCODING.contentEqualsIgnoreCase(headerName)
            &&
            !SipHeaderNames.TRAILER.contentEqualsIgnoreCase(headerName)) {
            trailer.trailingHeaders().add(headerName, value);
          }
          lastHeader = name;
          // reset name and value fields
          name = null;
          value = null;
        }

        line = headerParser.parse(buffer);
        if (line == null) {
          return null;
        }
      }
      while (line.length() > 0);

      this.trailer = null;
      return trailer;
    }

    return LastSipContent.EMPTY_LAST_CONTENT;
  }

  private static int getChunkSize(String hex) {
    hex = hex.trim();
    for (int i = 0; i < hex.length(); i++) {
      char c = hex.charAt(i);
      if ((c == ';') || Character.isWhitespace(c) || Character.isISOControl(c)) {
        hex = hex.substring(0, i);
        break;
      }
    }

    return Integer.parseInt(hex, 16);
  }

  private static String[] splitInitialLine(AppendableCharSequence sb) {
    int aStart;
    int aEnd;
    int bStart;
    int bEnd;
    int cStart;
    int cEnd;

    aStart = findNonWhitespace(sb, 0);
    aEnd = findWhitespace(sb, aStart);

    bStart = findNonWhitespace(sb, aEnd);
    bEnd = findWhitespace(sb, bStart);

    cStart = findNonWhitespace(sb, bEnd);
    cEnd = findEndOfString(sb);

    return new String[] {
      sb.subStringUnsafe(aStart, aEnd),
      sb.subStringUnsafe(bStart, bEnd),
      cStart < cEnd ? sb.subStringUnsafe(cStart, cEnd)
                    : "" };
  }

  private void splitHeader(AppendableCharSequence sb) {
    final int length = sb.length();
    int nameStart;
    int nameEnd;
    int colonEnd;
    int valueStart;
    int valueEnd;

    nameStart = findNonWhitespace(sb, 0);
    for (nameEnd = nameStart; nameEnd < length; nameEnd++) {
      char ch = sb.charAt(nameEnd);
      if ((ch == ':') || Character.isWhitespace(ch)) {
        break;
      }
    }

    for (colonEnd = nameEnd; colonEnd < length; colonEnd++) {
      if (sb.charAt(colonEnd) == ':') {
        colonEnd++;
        break;
      }
    }

    name = sb.subStringUnsafe(nameStart, nameEnd);
    valueStart = findNonWhitespace(sb, colonEnd);
    if (valueStart == length) {
      value = EMPTY_VALUE;
    }
    else {
      valueEnd = findEndOfString(sb);
      value = sb.subStringUnsafe(valueStart, valueEnd);
    }
  }

  private static int findNonWhitespace(AppendableCharSequence sb, int offset) {
    for (int result = offset; result < sb.length(); ++result) {
      if (!Character.isWhitespace(sb.charAtUnsafe(result))) {
        return result;
      }
    }
    return sb.length();
  }

  private static int findWhitespace(AppendableCharSequence sb, int offset) {
    for (int result = offset; result < sb.length(); ++result) {
      if (Character.isWhitespace(sb.charAtUnsafe(result))) {
        return result;
      }
    }
    return sb.length();
  }

  private static int findEndOfString(AppendableCharSequence sb) {
    for (int result = sb.length() - 1; result > 0; --result) {
      if (!Character.isWhitespace(sb.charAtUnsafe(result))) {
        return result + 1;
      }
    }
    return 0;
  }

  private static class HeaderParser implements ByteProcessor {
    private final AppendableCharSequence seq;
    private final int maxLength;
    private int size;

    HeaderParser(AppendableCharSequence seq, int maxLength) {
      this.seq = seq;
      this.maxLength = maxLength;
    }

    public AppendableCharSequence parse(ByteBuf buffer) {
      final int oldSize = size;
      seq.reset();
      int i = buffer.forEachByte(this);
      if (i == -1) {
        size = oldSize;
        return null;
      }
      buffer.readerIndex(i + 1);
      return seq;
    }

    public void reset() {
      size = 0;
    }

    @Override
    public boolean process(byte value) throws Exception {
      char nextByte = (char) (value & 0xFF);
      if (nextByte == SipConstants.CR) {
        return true;
      }
      if (nextByte == SipConstants.LF) {
        return false;
      }

      if (++size > maxLength) {
        // TODO: Respond with Bad Request and discard the traffic
        // or close the connection.
        // No need to notify the upstream handlers - just log.
        // If decoding a response, just throw an exception.
        throw newException(maxLength);
      }

      seq.append(nextByte);
      return true;
    }

    protected TooLongFrameException newException(int maxLength) {
      return new TooLongFrameException("SIP header is larger than " + maxLength + " bytes.");
    }
  }

  private static final class LineParser extends HeaderParser {

    LineParser(AppendableCharSequence seq, int maxLength) {
      super(seq, maxLength);
    }

    @Override
    public AppendableCharSequence parse(ByteBuf buffer) {
      reset();
      return super.parse(buffer);
    }

    @Override
    protected TooLongFrameException newException(int maxLength) {
      return new TooLongFrameException("An SIP line is larger than " + maxLength + " bytes.");
    }

  }

}
