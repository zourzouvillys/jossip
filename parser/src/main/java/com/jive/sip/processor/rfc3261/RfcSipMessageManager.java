package com.jive.sip.processor.rfc3261;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.base.api.RawMessage;
import com.jive.sip.message.SipMessage;
import com.jive.sip.message.SipRequest;
import com.jive.sip.message.SipResponse;
import com.jive.sip.message.SipResponseStatus;
import com.jive.sip.message.api.NameAddr;
import com.jive.sip.message.api.Reason;
import com.jive.sip.message.api.SipHeaderDefinition;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ParserInput;
import com.jive.sip.parsers.core.ByteParserInput;
import com.jive.sip.parsers.core.DefaultParserContext;
import com.jive.sip.parsers.core.ParameterParser;
import com.jive.sip.parsers.core.ParserUtils;
import com.jive.sip.processor.rfc3261.message.api.ResponseBuilder;
import com.jive.sip.processor.rfc3261.message.impl.SingleHeaderParseContext;
import com.jive.sip.processor.rfc3261.parsing.parsers.headers.NameAddrParser;
import com.jive.sip.processor.rfc3261.parsing.parsers.uri.UriParser;
import com.jive.sip.uri.SipUri;
import com.jive.sip.uri.Uri;

/**
 * Default internal implementation of the SIP message manager.
 *
 * @author theo
 *
 */

public class RfcSipMessageManager implements SipMessageManager {

  private static final RfcSipMessageManager DEFAULT_INSTANCE = new RfcSipMessageManagerBuilder().build();

  private static final String SIP_2_0 = "SIP/2.0";
  private static final char COLON = ':';
  private static final char SPACE = ' ';
  private static final String CRLF = "\r\n";

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
  public SipMessage convert(final RawMessage raw, final boolean lazy) {

    final DefaultSipMessage msg;

    if (raw.getInitialLine().startsWith(SIP_2_0)) {
      msg = this.createResponse(raw.getInitialLine(), raw.getHeaders());
    }
    else {
      msg = this.createRequest(raw.getInitialLine(), raw.getHeaders());
    }

    if (lazy == false) {
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
      final Collection<RawHeader> headers,
      final byte[] body) {
    return new DefaultSipRequest(this, method, ruri, SipMessage.VERSION, headers, body);
  }

  @Override
  public DefaultSipResponse createResponse(
      final SipResponseStatus status,
      final List<RawHeader> headers,
      final byte[] body) {
    return new DefaultSipResponse(this, SipMessage.VERSION, status, headers, body);
  }

  @Override
  public SipRequest createAck(final SipResponse res, final List<NameAddr> route) {

    final DefaultRequestBuilder builder = new DefaultRequestBuilder(this);

    builder.convertFromResponse(res);
    builder.setMethod(SipMethod.ACK);
    builder.setCSeq(res.getCSeq().withMethod(SipMethod.ACK));

    if ((route != null) && !route.isEmpty()) {
      builder.setRoute(route);
    }

    return builder.build();
  }

  @Override
  public SipRequest createCancel(final SipRequest original, final Reason reason) {

    final DefaultRequestBuilder builder = new DefaultRequestBuilder(this);

    builder.setRequestUri(original.getUri());
    builder.setTo(original.getTo());
    builder.setFrom(original.getFrom());
    builder.setCallID(original.getCallId());
    builder.setMethod(SipMethod.CANCEL);
    builder.setCSeq(original.getCSeq().withMethod(SipMethod.CANCEL));

    if (reason != null) {
      builder.setHeader("Reason", reason);
    }

    if ((original.getRoute() != null) && !original.getRoute().isEmpty()) {
      builder.setRoute(original.getRoute());
    }

    return builder.build();
  }

  @Override
  public SipRequest fromUri(final SipUri target, SipMethod defaultMethod) {
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

  public static SipMessageManager defaultInstance() {
    return DEFAULT_INSTANCE;
  }

}
