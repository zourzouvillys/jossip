package io.rtcore.sip.channels.connection;

import java.net.InetSocketAddress;

import javax.net.ssl.SSLSession;

import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.common.ImmutableHostPort;
import io.rtcore.sip.common.iana.StandardSipTransportName;

public class SipConnections {

  /**
   * the local address that received this packet.
   *
   * the address may not exist on the local instance, e.g because it's behind a NAT device.
   *
   */

  public static final SipAttributes.Key<InetSocketAddress> ATTR_LOCAL_ADDR = SipAttributes.Key.create("local-addr");

  /**
   * the address that we received this packet from. this may not be the underlying source IP
   * address, as sockets may generate it from other context, e.g a proxy protocol header or from a
   * geneve header.
   *
   * this may not exist in some cases, e.g when a message is generated locally.
   *
   */

  public static final SipAttributes.Key<InetSocketAddress> ATTR_REMOTE_ADDR = SipAttributes.Key.create("remote-addr");

  /**
   * any message transported over a TLS enabled session will include this to provide access to the
   * {@link SSLSession} that was used.
   */

  public static final SipAttributes.Key<SSLSession> ATTR_SSL_SESSION = SipAttributes.Key.create("ssl-session");

  // ---- [ STATEFUL LAYER ] ----

  /**
   * Represents the Via field value associated with a SIP message. This value is added by the SIP
   * transport when transmitting a request and removed from the response. It can be retrieved if
   * needed using this attribute.
   *
   * <p>
   * Note that not all transports or response generators will have this attribute. Locally generated
   * responses or those using an RPC mechanism instead of raw SIP encoding over the wire may not
   * have this attribute.
   *
   * <p>
   * When processing an incoming SIP request received over the network, the top Via field is not
   * removed from the message, and its value is set using this attribute.
   */

  // public static final SipAttributes.Key<Via> ATTR_VIA = SipAttributes.Key.create("local-via");

  /**
   * the branch value (without magic cookie) set for incoming and outgoing messages. it is set by
   * the transport layer before transmission, and extracted from responses. note that if the branch
   * does not contain a magic cookie value, then it will not be set.
   *
   * this may also be used for sending a CANCEL, the sender must set the branch-id to the same value
   * as the original INVITE, which is available once a 1xx response has been received.
   *
   */

  public static final SipAttributes.Key<String> ATTR_BRANCH_ID = SipAttributes.Key.create("branch-id");

  /**
   * the top Via sent-by for both incoming and outgoing messages. it is set by the transport layer
   * before transmission, and extracted from responses.
   *
   * this may also be used for sending a CANCEL, the sender must set the sent-by to the same value
   * as the original INVITE, which is available once a 1xx response has been received.
   *
   */

  public static final SipAttributes.Key<ImmutableHostPort> ATTR_SENT_BY = SipAttributes.Key.create("sent-by");

  /**
   * the top Via sent-by for both incoming and outgoing messages. it is set by the transport layer
   * before transmission, and extracted from responses.
   *
   * this may also be used for sending a CANCEL, the sender must set the sent-by to the same value
   * as the original INVITE, which is available once a 1xx response has been received.
   *
   */

  public static final SipAttributes.Key<StandardSipTransportName> ATTR_TRANSPORT = SipAttributes.Key.create("transport-protocol");

  /**
   * if the transport is websocket, the URI of the request.
   */

  public static final SipAttributes.Key<String> ATTR_WEBSOCKET_PATH = SipAttributes.Key.create("websocket-path");

  /**
   * if the transport is websocket, the Origin header.
   */

  public static final SipAttributes.Key<String> ATTR_WEBSOCKET_ORIGIN = SipAttributes.Key.create("websocket-origin");

  /**
   * if the transport is websocket, the Host header.
   */

  public static final SipAttributes.Key<String> ATTR_WEBSOCKET_HOST = SipAttributes.Key.create("websocket-host");

  /**
   * if the transport is websocket, the User-Agent header.
   */

  public static final SipAttributes.Key<String> ATTR_WEBSOCKET_USER_AGENT = SipAttributes.Key.create("websocket-user-agent");

}
