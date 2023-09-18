package io.rtcore.gateway.engine.sip;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Verify;

import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.connection.SipConnections;
import io.rtcore.sip.common.ImmutableHostPort;
import io.rtcore.sip.common.iana.SipMethodId;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.frame.SipRequestFrame;

public class InMemoryServerTransactionStore implements ServerTransactionStore {

  private static final Logger LOG = LoggerFactory.getLogger(InMemoryServerTransactionStore.class);

  /**
   * for requests that are in progress but not necessarily started a transaction, e.g doing some
   * async work. any branches in this map will be simply be dropped.
   */

  private final Set<ClientBranchId> absorbtion = ConcurrentHashMap.newKeySet();

  private static final Runnable NOTHING = () -> {
  };

  /**
   * do not use for ACK.
   */

  @Override
  public Action lookup(final SipRequestFrame req, final SipAttributes attrs) {

    // the request method.
    final SipMethodId method = req.initialLine().method();

    Verify.verify(method != SipMethods.ACK);

    final String branchId = attrs.get(SipConnections.ATTR_BRANCH_ID).orElse(null);
    final ImmutableHostPort sentBy = attrs.get(SipConnections.ATTR_SENT_BY).orElse(null);

    if ((sentBy == null) || (branchId == null)) {

      // we can only do a simple request-response without the ability to handle retransmits or such.
      // therefore, ignore the server transaction store completely.

      // TODO: make this a policy? might want to disable this in some (many?) deployments.

      // nothing to do with the store for this request, as we can't ever match a retransmit. so just
      // process with no removal handler.
      return Action.processAction(NOTHING);

    }

    final ClientBranchId branchKey = new ClientBranchId(sentBy, method, branchId);

    // no need to lock the absorbtion check as it's concurernt.

    if (!this.absorbtion.add(branchKey)) {
      // currently processing, so nothing to do except absorb the retransmission.
      return Action.absorbAction();
    }

    // at this point it is vital that we eventually remove from the absorbtion map.

    try {

      // check reply map. we do this while we're absorbing as the reply lookup may be done
      // asynchronously.

      // TODO: we should avoid leaking data from TLS over non TLS by sending the same branch on a
      // non TLS transport?

    }
    catch (final Throwable t) {
      this.absorbtion.remove(branchKey);
      throw t;
    }

    return Action.processAction(() -> {
      this.absorbtion.remove(branchKey);
      LOG.info("removed {} from absorbtion map", branchKey);
    });

  }

  @Override
  public int absorbtionSize() {
    return this.absorbtion.size();
  }

}
