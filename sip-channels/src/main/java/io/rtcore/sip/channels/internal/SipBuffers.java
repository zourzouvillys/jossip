package io.rtcore.sip.channels.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.eclipse.jdt.annotation.Nullable;

public final class SipBuffers {

  // note: these are the same as gRPCs interfaces of the same names.

  private SipBuffers() {
  }

  /**
   * An {@link java.io.InputStream} or alike whose total number of bytes that can be read is known
   * upfront.
   *
   * <p>
   * Usually it's a {@link java.io.InputStream} that also implements this interface, in which case
   * {@link java.io.InputStream#available()} has a stronger semantic by returning an accurate number
   * instead of an estimation.
   */

  public interface KnownLength {

    /**
     * Returns the total number of bytes that can be read (or skipped over) from this object until
     * all bytes have been read out.
     */

    int available() throws IOException;

  }

  /**
   * Extension to an {@link java.io.InputStream} whose content can be accessed as
   * {@link ByteBuffer}s.
   *
   * <p>
   * This can be used for optimizing the case for the consumer of a {@link ByteBuffer}-backed input
   * stream supports efficient reading from {@link ByteBuffer}s directly. This turns the reader
   * interface from an {@link java.io.InputStream} to {@link ByteBuffer}s, without copying the
   * content to a byte array and read from it.
   */

  public interface HasByteBuffer {

    /**
     * Indicates whether or not {@link #getByteBuffer} operation is supported.
     */

    boolean byteBufferSupported();

    /**
     * Gets a {@link ByteBuffer} containing some bytes of the content next to be read, or {@code
     * null} if has reached end of the content. The number of bytes contained in the returned buffer
     * is implementation specific. Calling this method does not change the position of the input
     * stream. The returned buffer's content should not be modified, but the position, limit, and
     * mark may be changed. Operations for changing the position, limit, and mark of the returned
     * buffer does not affect the position, limit, and mark of this input stream. This is an
     * optional method, so callers should first check {@link #byteBufferSupported}.
     *
     * @throws UnsupportedOperationException
     *           if this operation is not supported.
     */

    @Nullable
    ByteBuffer getByteBuffer();

  }

  /**
   * An extension of {@link InputStream} that allows the underlying data source to be detached and
   * transferred to a new instance of the same kind. The detached InputStream takes over the
   * ownership of the underlying data source. That's said, the detached InputStream is responsible
   * for releasing its resources after use. The detached InputStream preserves internal states of
   * the underlying data source. Data can be consumed through the detached InputStream as if being
   * continually consumed through the original instance. The original instance discards internal
   * states of detached data source and is no longer consumable as if the data source is exhausted.
   *
   * <p>
   * A normal usage of this API is to extend the lifetime of the data source owned by the original
   * instance for doing extra processing before releasing it. For example, when combined with
   * {@link HasByteBuffer}, a custom Marshaller can take over the ownership of buffers containing
   * inbound data and perform delayed deserialization.
   */

  public interface Detachable {

    /**
     * Detaches the underlying data source from this instance and transfers to an
     * {@link InputStream}. Detaching data from an already-detached instance gives an InputStream
     * with zero bytes of data.
     */
    InputStream detach();

  }

}
