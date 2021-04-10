package io.rtcore.sip.sigcore.txn;

import org.immutables.value.Value;

import io.rtcore.sip.sigcore.Address;
import io.rtcore.sip.sigcore.txn.StateMachine.Event;

@Value.Enclosing
public class TransactionMessages {

  @Value.Immutable
  public interface StartTransaction extends Event<StartTransaction> {

    @Override
    default StartTransaction payload() {
      return this;
    }

    @Value.Parameter
    RxSipFrame request();

    @Value.Parameter
    Address transport();

  }

  public static StartTransaction startTransaction(RxSipFrame request, Address transport) {
    return ImmutableTransactionMessages.StartTransaction.of(request, transport);
  }

  public static class ProvisionalResponse implements Event<RxSipFrame> {

    private final RxSipFrame response;

    public ProvisionalResponse(RxSipFrame response) {
      this.response = response;
    }

    @Override
    public RxSipFrame payload() {
      return this.response;
    }

  }

  public abstract static class FinalResponse implements Event<RxSipFrame> {
  }

  public static class SuccessResponse extends FinalResponse {

    private final RxSipFrame response;

    public SuccessResponse(RxSipFrame response) {
      this.response = response;
    }

    @Override
    public RxSipFrame payload() {
      return this.response;
    }

  }

  public static class RejectResponse extends FinalResponse {

    private final RxSipFrame response;

    public RejectResponse(RxSipFrame response) {
      this.response = response;
    }

    @Override
    public RxSipFrame payload() {
      return this.response;
    }

  }

  /**
   * signals a transport related error while attempting to send the request and before any response
   * is received.
   * 
   * this is only signaled at that Calling state, as once we've got confirmation of sending we can
   * not treat a transport error as a failure of the transaction due to a response being sent in
   * some other channel, e.g re-opening the connection back to us.
   * 
   */

  public static class TransportError implements Event<Exception> {

    private final Exception exception;

    public TransportError(Exception exception) {
      this.exception = exception;
    }

    @Override
    public Exception payload() {
      return this.exception;
    }

  }

}
