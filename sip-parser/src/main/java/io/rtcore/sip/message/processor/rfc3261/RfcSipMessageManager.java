package io.rtcore.sip.message.processor.rfc3261;

import java.net.URI;
import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipMethodId;
import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.base.api.RawMessage;
import io.rtcore.sip.message.message.SipMessage;
import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.SipResponseStatus;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.message.api.Reason;
import io.rtcore.sip.message.message.api.SipHeaderDefinition;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ParserInput;
import io.rtcore.sip.message.parsers.core.ByteParserInput;
import io.rtcore.sip.message.parsers.core.DefaultParserContext;
import io.rtcore.sip.message.parsers.core.ParameterParser;
import io.rtcore.sip.message.parsers.core.ParserUtils;
import io.rtcore.sip.message.processor.rfc3261.message.api.ResponseBuilder;
import io.rtcore.sip.message.processor.rfc3261.message.impl.SingleHeaderParseContext;
import io.rtcore.sip.message.processor.rfc3261.parsing.RfcMessageParserBuilder;
import io.rtcore.sip.message.processor.rfc3261.parsing.RfcSipMessageParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.NameAddrParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.uri.UriParser;
import io.rtcore.sip.message.uri.SipUri;
import io.rtcore.sip.message.uri.Uri;

/**
 * Default internal implementation of the SIP message manager.
 *
 *
 *
 */

public class RfcSipMessageManager implements SipMessageManager {

  private static final RfcSipMessageManager DEFAULT_INSTANCE = new RfcSipMessageManagerBuilder().build();
  private static final RfcSipMessageParser DEFAULT_MESSAGE_PARSER = new RfcMessageParserBuilder().build();

  private static final String SIP_2_0 = "SIP/2.0";

  // / max length of the initial line in a request. includes URI, so potentially
  // longer.
  private static final int MAX_REQUEST_LINE = 1024;

  // / max length of the initial line in a response
  private static final int MAX_RESPONSE_LINE = 512;

  private final Map<String, SipHeaderDefinition<?>> headers = Maps.newHashMap();
  private SipMessageManagerListener listener = null;

  public RfcSipMessageManager() {

  }

  void register(final SipHeaderDefinition<?> def) {

    this.headers.put(def.getName().toLowerCase(), def);

    if (def.getShortName().isPresent()) {
      this.headers.put(Character.toString(def.getShortName().get()).toLowerCase(), def);
    }

  }

  @Override
  public SipMessage convert(final RawMessage raw) {
    return this.convert(raw, true);
  }

  @Override
  public SipMessage parseMessage(final ByteBuffer buf) {
    final RawMessage raw = DEFAULT_MESSAGE_PARSER.parse(buf);
    return this.convert(raw, true);
  }

  @Override
  public RawMessage parseRawMessage(final ByteBuffer buf) {
    return DEFAULT_MESSAGE_PARSER.parse(buf);
  }

  @Override
  public SipMessage convert(final RawMessage raw, final boolean lazy) {

    final DefaultSipMessage msg;

    if (raw.getInitialLine().startsWith(SIP_2_0)) {
      msg = this.createResponse(raw.getInitialLine(), raw.getHeaders());
    }
    else {
      msg = this.createRequest(raw.getInitialLine(), raw.getHeaders());
    }

    if (!lazy) {
      msg.validate();
    }

    // handle the body
    if (raw.getBody() != null) {
      msg.setBody(raw.getBody());
    }

    return msg;

  }

  private DefaultSipResponse createResponse(final String line, final Collection<RawHeader> headers) {

    Preconditions.checkArgument(line.length() < MAX_RESPONSE_LINE);

    final ParserInput input = ByteParserInput.fromString(line);

    final ParserContext context = new DefaultParserContext(input);

    context.read(ParserUtils.SIP_VERSION);

    context.read(ParserUtils.SP);

    final int code = context.read(ParserUtils.name(ParserUtils._3DIGIT, "SIP status code"));

    if ((code < 100) || (code > 699)) {
      throw new RuntimeException(String.format("Invalid Status Code '%d'", code));
    }

    context.read(ParserUtils.SP);

    String reason = (String) context.subSequence(context.position(), context.limit());

    if (reason.length() == 0) {
      reason = null;
    }

    return new DefaultSipResponse(this, SipMessage.VERSION, new SipResponseStatus(code, reason), headers);

  }

  private static final Parser<Uri> RURI = ParserUtils.name(UriParser.URI, "R-URI");
  private static final Parser<CharSequence> REQUEST_METHOD = ParserUtils.name(ParserUtils.TOKEN, "request method");

  DefaultSipRequest createRequest(final String line, final Collection<RawHeader> headers) {

    Preconditions.checkArgument(line.length() < MAX_REQUEST_LINE);

    final ParserInput input = ByteParserInput.fromString(line);

    final ParserContext context = new DefaultParserContext(input);

    final CharSequence method = context.read(REQUEST_METHOD);

    context.read(ParserUtils.SP);

    final Uri uri = context.read(RURI);

    context.read(ParserUtils.SP);

    context.read(ParserUtils.SIP_VERSION);

    return new DefaultSipRequest(this, SipMethod.of(method), uri, SipMessage.VERSION, headers, null);

  }

  @Override
  public ResponseBuilder responseBuilder(final SipResponseStatus status) {
    return new DefaultResponseBuilder(this, status);
  }

  /**
   * Looks up the correct parser to use for the given header name.
   *
   * Throws if header is not found.
   *
   * @param headerName
   * @return
   */

  public <T> SipHeaderDefinition<T> getParser(final String headerName, final SipHeaderDefinition<T> defaultParser) {

    final SipHeaderDefinition<T> def = (SipHeaderDefinition<T>) this.headers.get(headerName.toLowerCase());

    if (def == null) {
      return defaultParser;
    }

    return def;

  }

  public <T> SipHeaderDefinition<T> getParser(final String headerName) {

    final SipHeaderDefinition<T> def = this.getParser(headerName, null);

    if (def == null) {

      if (this.listener != null) {
        return (SipHeaderDefinition<T>) this.listener.unknownHeader(headerName);
      }

      throw new RuntimeException(MessageFormat.format("Unknown Header ''{0}''", headerName));

    }

    return def;

  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T adapt(final Class<T> adapter) {
    if (adapter.isAssignableFrom(RfcSipMessageManager.class)) {
      return (T) this;
    }
    throw new RuntimeException("Unable to create adapter to " + adapter.getName());
  }

  public HeaderParseContext createParseContext(final String text) {
    return new SingleHeaderParseContext(this, null, text);
  }

  public void addListener(final SipMessageManagerListener listener) {
    this.listener = listener;
  }

  @Override
  public DefaultSipRequest createRequest(
      final SipMethod method,
      final Uri ruri,
      final Iterable<RawHeader> headers,
      final byte[] body) {
    return new DefaultSipRequest(this, method, ruri, SipMessage.VERSION, headers, body);
  }

  @Override
  public DefaultSipResponse createResponse(
      final SipResponseStatus status,
      final Iterable<RawHeader> headers,
      final byte[] body) {
    return new DefaultSipResponse(this, SipMessage.VERSION, status, headers, body);
  }

  @Override
  public SipRequest createAck(final SipResponse res, final List<NameAddr> route) {

    final DefaultRequestBuilder builder = new DefaultRequestBuilder(this);

    builder.convertFromResponse(res);
    builder.setMethod(SipMethod.ACK);
    builder.setCSeq(res.cseq().withMethod(SipMethod.ACK));

    if ((route != null) && !route.isEmpty()) {
      builder.setRoute(route);
    }

    return builder.build();
  }

  @Override
  public SipRequest createCancel(final SipRequest original, final Reason reason) {

    final DefaultRequestBuilder builder = new DefaultRequestBuilder(this);

    builder.setRequestUri(original.uri());
    builder.setTo(original.to());
    builder.setFrom(original.from());
    builder.setCallID(original.callId());
    builder.setMethod(SipMethod.CANCEL);
    builder.setCSeq(original.cseq().withMethod(SipMethod.CANCEL));

    if (reason != null) {
      builder.setHeader("Reason", reason);
    }

    if ((original.route() != null) && !original.route().isEmpty()) {
      builder.setRoute(original.route());
    }

    return builder.build();
  }

  @Override
  public SipRequest fromUri(final SipUri target, final SipMethod defaultMethod) {
    final SipMethod method = target.getParameter(SipUri.PMethod).map(SipMethod.tokenConverter()).orElse(defaultMethod);
    return this.createRequest(method,
      target.withoutHeaders().withoutParameter(SipUri.PMethod),
      target.getHeaders(),
      null);
  }

  @Override
  public Uri parseUri(final String str) {
    final ParserInput input = ByteParserInput.fromString(str);
    final ParserContext context = new DefaultParserContext(input);
    final Uri uri = context.read(RURI);
    if (input.remaining() > 0) {
      throw new RuntimeException("Trailing data at end of URI");
    }
    return uri;
  }

  @Override
  public Parameters parseParameters(final String input) {

    final ByteParserInput is = ByteParserInput.fromString(input);
    final Collection<RawParameter> value = ParserUtils.read(is, ParameterParser.getInstance());

    if (is.remaining() > 0) {
      throw new RuntimeException("Trailing data at end of parameters");
    }

    return DefaultParameters.from(value);

  }

  @Override
  public NameAddr parseNameAddr(final String na) {
    final ParserInput input = ByteParserInput.fromString(na);
    final ParserContext context = new DefaultParserContext(input);
    final NameAddr res = context.read(NameAddrParser.INSTANCE);
    if (input.remaining() > 0) {
      throw new RuntimeException("Trailing data at end of Name-Addr");
    }
    return res;
  }

  public static RfcSipMessageManager defaultInstance() {
    return DEFAULT_INSTANCE;
  }

  public SipRequest parseRequest(final SipMethodId method, final URI ruri, final List<SipHeaderLine> headers, final Optional<String> body) {
    return new DefaultSipRequest(
      this,
      SipMethod.fromString(method.token()),
      this.parseUri(ruri.toString()),
      SipMessage.VERSION,
      Lists.transform(headers, h -> new RawHeader(h.headerName(), h.headerValues())),
      body.map(String::getBytes).orElse(new byte[0]));
  }

}
