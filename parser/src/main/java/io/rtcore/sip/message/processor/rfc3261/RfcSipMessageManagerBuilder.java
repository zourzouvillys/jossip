package io.rtcore.sip.message.processor.rfc3261;

import java.util.Set;

import com.google.common.collect.Sets;

import io.rtcore.sip.message.message.api.SipHeaderDefinition;
import io.rtcore.sip.message.processor.rfc3261.message.impl.SingleHeaderDefinition;

public class RfcSipMessageManagerBuilder {

  private SipMessageManagerListener listener = null;
  private final Set<SipHeaderDefinition<?>> headers = Sets.newHashSet();

  public RfcSipMessageManager build() {

    final RfcSipMessageManager manager = new RfcSipMessageManager();

    manager.addListener(this.listener);

    registerDefaultHeaders(manager);

    for (final SipHeaderDefinition<?> def : this.headers) {
      manager.register(def);
    }

    return manager;

  }

  private void registerDefaultHeaders(final RfcSipMessageManager manager) {

    manager.register(DefaultSipMessage.ACCEPT);
    manager.register(DefaultSipMessage.ALERT_INFO);
    manager.register(DefaultSipMessage.AUTHORIZATION);
    manager.register(DefaultSipMessage.CSEQ);
    manager.register(DefaultSipMessage.CALL_ID);
    manager.register(DefaultSipMessage.CONTACT);
    manager.register(DefaultSipMessage.CONTENT_LENGTH);
    manager.register(DefaultSipMessage.CONTENT_TYPE);
    manager.register(DefaultSipMessage.DATE);
    manager.register(DefaultSipMessage.ERROR_INFO);
    manager.register(DefaultSipMessage.EVENT);
    manager.register(DefaultSipMessage.EXPIRES);
    manager.register(DefaultSipMessage.FROM);
    manager.register(DefaultSipMessage.MIME_VERSION);
    manager.register(DefaultSipMessage.MAX_FORWARDS);
    manager.register(DefaultSipMessage.PROXY_AUTHENTICATE);
    manager.register(DefaultSipMessage.PROXY_AUTHORIZATION);
    manager.register(DefaultSipMessage.PROXY_REQUIRE);
    manager.register(DefaultSipMessage.RECORD_ROUTE);
    manager.register(DefaultSipMessage.REFER_TO);
    manager.register(DefaultSipMessage.RETRY_AFTER);
    manager.register(DefaultSipMessage.REQUIRE);
    manager.register(DefaultSipMessage.ROUTE);
    manager.register(DefaultSipMessage.SUBJECT);
    manager.register(DefaultSipMessage.TO);
    manager.register(DefaultSipMessage.UNSUPPORTED);
    manager.register(DefaultSipMessage.VIA);
    manager.register(DefaultSipMessage.WWW_AUTHENTICATE);

    // register all of our unknown headers for now.
    manager.register(SingleHeaderDefinition.create("Warning"));
    manager.register(SingleHeaderDefinition.create("User-Agent"));
    manager.register(SingleHeaderDefinition.create("Supported", 'k'));
    manager.register(SingleHeaderDefinition.create("Content-Disposition"));
    manager.register(SingleHeaderDefinition.create("Allow"));
    manager.register(SingleHeaderDefinition.create("Accept-Encoding"));
    manager.register(SingleHeaderDefinition.create("Allow-Events", 'u'));
    manager.register(SingleHeaderDefinition.create("Accept-Language"));
    manager.register(SingleHeaderDefinition.create("Subscription-State"));
    manager.register(SingleHeaderDefinition.create("Remote-Party-ID"));
    manager.register(SingleHeaderDefinition.create("Privacy"));
    manager.register(SingleHeaderDefinition.create("Server"));

    manager.register(SingleHeaderDefinition.create("Content-Transfer-Encoding"));
    manager.register(SingleHeaderDefinition.create("Identity"));
    manager.register(SingleHeaderDefinition.create("Min-Expires"));

    // temporary headers we know nothing about (e.g, non-standards).
    // this.register(SingleHeaderDefinition.create("UnknownHeaderWithUnusualValue"));
    // this.register(SingleHeaderDefinition.create("NewFangledHeader"));
    // this.register(SingleHeaderDefinition.create("C%6Fntact"));
    // this.register(SingleHeaderDefinition.create("extensionHeader-!.%*+_`'~"));
    // this.register(SingleHeaderDefinition.create("Unknown-LongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLong-Name"));
    // this.register(SingleHeaderDefinition.create("X-jid"));
    // this.register(SingleHeaderDefinition.create("X-Real-IP"));
    // this.register(SingleHeaderDefinition.create("Diversion"));
    // this.register(SingleHeaderDefinition.create("P-hint"));
    // this.register(SingleHeaderDefinition.create("P-Station-Name"));
    // this.register(SingleHeaderDefinition.create("P-RTP-Stat"));
    // this.register(SingleHeaderDefinition.create("ms-keep-alive"));
    // this.register(SingleHeaderDefinition.create("X-Grandstream-PBX"));
    // this.register(SingleHeaderDefinition.create("Aastra-Mac"));
    // this.register(SingleHeaderDefinition.create("Mac"));
    // this.register(SingleHeaderDefinition.create("X-QuteCom-Ping"));
    // this.register(SingleHeaderDefinition.create("WWW-Contact"));
    // this.register(SingleHeaderDefinition.create("Line"));
    // this.register(SingleHeaderDefinition.create("X-Asterisk-HangupCause"));
    // this.register(SingleHeaderDefinition.create("X-Asterisk-HangupCauseCode"));

  }

  /**
   * Adds a new {@link SipHeaderDefinition} to be registered with the {@link SipMessageManager}
   * instance.
   * 
   * @param def
   * @return
   * 
   */

  public <T> RfcSipMessageManagerBuilder addHeader(final SipHeaderDefinition<T> def) {
    // TODO: TPZ: check we're not overriding one already set (match both name and short name).
    this.headers.add(def);
    return this;
  }

  public RfcSipMessageManagerBuilder addListener(final SipMessageManagerListener listener) {
    this.listener = listener;
    return this;
  }

}
