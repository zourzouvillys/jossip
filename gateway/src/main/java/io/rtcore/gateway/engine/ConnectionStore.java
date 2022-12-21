package io.rtcore.gateway.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rtcore.sip.channels.connection.SipConnection;
import io.rtcore.sip.channels.netty.NettySipAttributes;

public class ConnectionStore {

  private static final Logger log = LoggerFactory.getLogger(ConnectionStore.class);

  private final Map<String, SipConnection> channels = new HashMap<>();

  public Optional<SipConnection> lookup(final String id) {
    return Optional.ofNullable(this.channels.get(id));
  }

  public void add(final SipConnection conn) {
    final String id = conn.attributes().get(NettySipAttributes.ATTR_CHANNEL).map(ch -> ch.id().asShortText()).orElseThrow();
    this.channels.put(id, conn);
  }

  public void remove(final SipConnection conn) {
    final String id = conn.attributes().get(NettySipAttributes.ATTR_CHANNEL).map(ch -> ch.id().asShortText()).orElseThrow();
    if (!this.channels.remove(id, conn)) {
      log.warn("unable to remove channel {}, {}", id, conn);
    }
  }

}
