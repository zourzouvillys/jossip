package io.rtcore.sip.channels.stub;

public interface ClientCallStreamObserver {

  /**
   * Prevent any further processing for this {@code ClientCallStreamObserver}. No further messages
   * will be received. The server is informed of cancellations, but may not stop processing the
   * call. Cancelling an already {@code cancel()}ed {@code ClientCallStreamObserver} has no effect.
   *
   * <p>
   * No other methods on this class can be called after this method has been called.
   *
   * <p>
   * It is recommended that at least one of the arguments to be non-{@code null}, to provide useful
   * debug information. Both argument being null may log warnings and result in suboptimal
   * performance. Also note that the provided information will not be sent to the server.
   *
   * @param message
   *          if not {@code null}, will appear as the description of the CANCELLED status
   * @param cause
   *          if not {@code null}, will appear as the cause of the CANCELLED status
   */

  public abstract void cancel(String message, Throwable cause);

  /**
   * Requests the peer to produce {@code count} more messages to be delivered to the 'inbound'
   * {@link StreamObserver}.
   *
   * <p>
   * This method is safe to call from multiple threads without external synchronization.
   *
   * @param count
   *          more messages
   */

  // @Override
  // public abstract void request(int count);

}
