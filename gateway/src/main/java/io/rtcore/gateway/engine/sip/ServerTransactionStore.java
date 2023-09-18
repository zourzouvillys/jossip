package io.rtcore.gateway.engine.sip;

import java.util.concurrent.atomic.AtomicReference;

import com.google.common.base.Preconditions;

import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.frame.SipResponseFrame;

public interface ServerTransactionStore {

  sealed interface Action permits AbsorbAction, ReplyAction, ProcessAction {

    static AbsorbAction absorbAction() {
      return AbsorbAction.INSTANCE;
    }

    static ReplyAction replyAction(final SipResponseFrame res) {
      return new ReplyAction(res);
    }

    static ProcessAction processAction(final Runnable unregister) {
      return new ProcessAction(unregister);
    }

  }

  /**
   * action to indicate that the request should be absorbed (ignored).
   */

  public static final class AbsorbAction implements Action {

    public static final AbsorbAction INSTANCE = new AbsorbAction();

    private AbsorbAction() {
    }

  }

  public static record ReplyAction(SipResponseFrame response) implements Action {

  }

  public static final class ProcessAction implements Action {

    private final AtomicReference<Runnable> unregister;

    private ProcessAction(final Runnable unregister) {
      this.unregister = new AtomicReference<>(unregister);
    }

    /**
     * will run the handler within the context of the branch, and remove the absorption once
     * completed.
     */

    public void run(final Runnable handler) {

      final Runnable un = this.unregister.getAndSet(null);

      Preconditions.checkState(un != null, "processor already completed");

      try {
        handler.run();
      }
      finally {
        un.run();
      }
    }

  }

  /**
   * lookup a policy request.
   *
   * @param req
   * @param attrs
   *
   */

  Action lookup(SipRequestFrame req, SipAttributes attrs);

  int absorbtionSize();

}
