package io.rtcore.sip.channels.connection;

import java.net.SocketAddress;

import javax.net.ssl.SSLSession;

import io.rtcore.sip.channels.internal.SipAttributes;
import io.rtcore.sip.common.ImmutableHostPort;
import io.rtcore.sip.common.iana.StandardSipTransportName;

public class SipConnections {

  /**
   * the local address that received this packet.
   *
   * the address may not exist on the local instance, e.g because it's behind a NAT device.
   *
   */

  public static final SipAttributes.Key<SocketAddress> ATTR_LOCAL_ADDR = SipAttributes.Key.create("local-addr");

  /**
   * the address that we received this packet from. this may not be the underlying source IP
   * address, as sockets may generate it from other context, e.g a proxy protocol header or from a
   * geneve header.
   *
   * this may not exist in some cases, e.g when a message is generated locally.
   *
   */

  public static final SipAttributes.Key<SocketAddress> ATTR_REMOTE_ADDR = SipAttributes.Key.create("remote-addr");

  /**
   * any message transported over a TLS enabled session will include this to provide access to the
   * {@link SSLSession} that was used.
   */

  public static final SipAttributes.Key<SSLSession> ATTR_SSL_SESSION = SipAttributes.Key.create("ssl-session");

  // ---- [ STATEFUL LAYER ] ----

  /**
   * a raw SIP transport will add a Via header when transmitting a request, and remove it from the
   * response. it can be retrieved if needed using this attribute.
   *
   * note that not all transports or response generators will have this attribute - locally
   * generated responses, or those using an RPC mechanism instead of raw SIP encoding over the wire
   * may not have one.
   *
   * an incoming SIP request (over the network) will not remove the top Via.
   *
   */

  // public static final SipAttributes.Key<Via> ATTR_VIA = SipAttributes.Key.create("local-via");

  /**
   * the branch value (without magic cookie) set for incoming and outgoing messages. it is set by
   * the transport layer before transmission, and extracted from responses.
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

}
