package io.rtcore.gateway.engine.http;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.google.auto.service.AutoService;
import com.google.common.collect.Iterables;
import com.google.common.net.InetAddresses;

import io.rtcore.gateway.engine.BackendProvider;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.connection.SipConnections;
import io.rtcore.sip.channels.netty.NettySipAttributes;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.common.iana.StandardSipTransportName;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.frame.SipResponseFrame;
import io.rtcore.sip.netty.codec.SipParsingUtils;

@AutoService(value = { BackendProvider.class })
public class HttpBackendProvider implements BackendProvider {

  private static final Logger logger = LoggerFactory.getLogger(HttpBackendProvider.class);

  private final String baseURI;
  private final String selfbase;

  private final String serverId;

  public HttpBackendProvider() throws UnknownHostException {
    this.serverId = InetAddress.getLocalHost().getHostName().toLowerCase();
    this.selfbase = String.format("http://%s:1188", this.serverId);
    logger.info("self is {}", this.selfbase);
    this.baseURI = Optional.ofNullable(System.getenv("TU_BASE")).orElse("http://localhost:8080/edge");
    logger.info("TU baseURI is {}", this.baseURI);
  }

  @Override
  public HttpCallMapper createMapper(final SipRequestFrame req, final SipAttributes attrs) {

    logger.debug("request attributes: {}", attrs);

    final Map<String, ValueNode> properties = new HashMap<>();

    final String connectionId = attrs.get(NettySipAttributes.ATTR_CHANNEL).map(ch -> ch.id().asShortText()).orElseThrow();
    final InetSocketAddress remoteAddr = attrs.get(SipConnections.ATTR_REMOTE_ADDR).orElseThrow();
    final StandardSipTransportName transport = attrs.get(SipConnections.ATTR_TRANSPORT).orElseThrow();

    properties.put("server.instance", JsonNodeFactory.instance.textNode(this.serverId));
    properties.put("transport", JsonNodeFactory.instance.textNode(transport.id()));
    properties.put("connectionId", JsonNodeFactory.instance.textNode(connectionId));
    properties.put("remote",
      JsonNodeFactory.instance.textNode(String.format("%s:%s", InetAddresses.toUriString(remoteAddr.getAddress()), remoteAddr.getPort())));

    attrs.get(SipConnections.ATTR_WEBSOCKET_PATH).ifPresent(value -> properties.put("websocket.path", JsonNodeFactory.instance.textNode(value)));
    attrs.get(SipConnections.ATTR_WEBSOCKET_ORIGIN).ifPresent(value -> properties.put("websocket.origin", JsonNodeFactory.instance.textNode(value)));
    attrs.get(SipConnections.ATTR_WEBSOCKET_HOST).ifPresent(value -> properties.put("websocket.host", JsonNodeFactory.instance.textNode(value)));
    attrs.get(SipConnections.ATTR_WEBSOCKET_USER_AGENT).ifPresent(value -> properties.put("websocket.user-agent", JsonNodeFactory.instance.textNode(value)));

    return new InviteMapper(URI.create(String.format("%s/%s", this.baseURI, this.path(req, attrs))), req, attrs, properties);

  }

  private String path(final SipRequestFrame req, final SipAttributes attrs) {

    final SipMethods method = req.initialLine().method().toStandard();

    if (method == SipMethods.REGISTER) {
      return "register";
    }

    // final boolean routed = SipFrameUtils.hasHeader(req.headerLines(), StandardSipHeaders.ROUTE);

    final boolean toTag = SipParsingUtils.toTag(req.headerLines()).isPresent();

    if (toTag) {
      return "in-dialog";
    }

    return switch (method) {
      case INVITE, ACK, CANCEL -> "invite";
      case SUBSCRIBE -> "subscribe";
      case REFER -> "refer";
      case OPTIONS -> "options";
      case PUBLISH -> "publish";

      // these should be in-dialog, but are sent by some clients/usages out of dialog.
      case NOTIFY -> "notify";
      case MESSAGE -> "message";
      case INFO -> "info";

      // unknown request?
      default -> "request";
    };

  }

  @Override
  public UnaryOperator<SipResponseFrame> serverResponseInterceptor() {
    return res -> res.withHeaderLines(Iterables.concat(res.headerLines(), List.of(SipHeaderLine.of("X-RTCore-Instance", this.serverId))));
  }

}
