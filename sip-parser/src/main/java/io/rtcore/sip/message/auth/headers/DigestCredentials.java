package io.rtcore.sip.message.auth.headers;

import java.util.Optional;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.QuotedString;
import io.rtcore.sip.message.parameters.api.SipParameterDefinition;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parameters.tools.ParameterUtils;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.AuthorizationParser;

public class DigestCredentials extends Authorization {

  public static DigestValues.Builder builder() {
    return DigestValues.builder();
  }

  protected static final SipParameterDefinition<String> USERNAME = ParameterUtils.createQuotedStringParameterDefinition("username");
  protected static final SipParameterDefinition<Token> ALGORITHM = ParameterUtils.createTokenParameterDefinition("algorithm");
  protected static final SipParameterDefinition<String> CNONCE = ParameterUtils.createQuotedStringParameterDefinition("cnonce");
  protected static final SipParameterDefinition<String> NONCE = ParameterUtils.createQuotedStringParameterDefinition("nonce");
  protected static final SipParameterDefinition<String> OPAQUE = ParameterUtils.createQuotedStringParameterDefinition("opaque");
  protected static final SipParameterDefinition<String> QOP = ParameterUtils.createQuotedStringParameterDefinition("qop");
  protected static final SipParameterDefinition<Token> NONCE_COUNT = ParameterUtils.createTokenParameterDefinition("nc");
  protected static final SipParameterDefinition<String> DIGEST_URI = ParameterUtils.createQuotedStringParameterDefinition("uri");
  protected static final SipParameterDefinition<String> RESPONSE = ParameterUtils.createQuotedStringParameterDefinition("response");
  protected static final SipParameterDefinition<String> REALM = ParameterUtils.createQuotedStringParameterDefinition("realm");
  protected static final SipParameterDefinition<String> DOMAIN = ParameterUtils.createQuotedStringParameterDefinition("domain");
  protected static final SipParameterDefinition<Token> STALE = ParameterUtils.createTokenParameterDefinition("stale");

  public DigestCredentials() {
    this(DefaultParameters.EMPTY);
  }

  public DigestCredentials(final Parameters parameters) {
    super("Digest", parameters);
  }

  @Override
  public <T> Optional<T> as(Class<T> klass) {
    if (klass.isInstance(this)) {
      return Optional.of(klass.cast(this));
    }
    return super.as(klass);
  }

  @Override
  public DigestCredentials withParameters(final Parameters parameters) {
    return new DigestCredentials(parameters);
  }

  /**
   *
   */

  public String realm() {
    return this.getParameter(REALM).orElse(null);
  }

  public String domain() {
    return this.getParameter(DOMAIN).orElse(null);
  }

  public String nonce() {
    return this.getParameter(NONCE).orElse(null);
  }

  public String username() {
    return this.getParameter(USERNAME).orElse(null);
  }

  public String opqaue() {
    return this.getParameter(OPAQUE).orElse(null);
  }

  public boolean stale() {
    return this.getParameter(STALE).map(b -> b.toString().equals("true")).orElse(false);
  }

  public String algorithm() {
    return this.getParameter(ALGORITHM).map(e -> e.toString()).orElse(null);
  }

  public String digestUri() {
    return this.getParameter(DIGEST_URI).orElse(null);
  }

  public String response() {
    return this.getParameter(RESPONSE).map(t -> t.toString()).orElse(null);
  }

  public String cnonce() {
    return this.getParameter(CNONCE).orElse(null);
  }

  public String qop() {
    return this.getParameter(QOP).orElse(null);
  }

  public String opaque() {
    return this.getParameter(OPAQUE).orElse(null);
  }

  /**
   * null if not set.
   */

  public Long nonceCount() {
    return this.getParameter(NONCE_COUNT).map(e -> Long.parseLong(e.toString())).orElse(null);
  }

  /**
   * null if not set.
   */

  public String nc() {
    return this.getParameter(NONCE_COUNT).map(Token::asString).orElse(null);
  }

  /**
   * returns an instance with an incremented nonce count.
   */

  public DigestCredentials withIncrementedCnonceCount() {
    Token nc =
      Token.from(String.format("%08d",
        (this.nonceCount() == null) ? Token.from(1)
                                    : Token.from(this.nonceCount() + 1)));
    return (DigestCredentials) this.withParameter(NONCE_COUNT.name(), nc);
  }

  public DigestCredentials withResponse(String authorization) {
    return (DigestCredentials) this.withParameter(RESPONSE.name(), QuotedString.from(authorization)).withoutParameter(STALE.name());
  }

  public DigestCredentials withCnonce(String cnonce) {
    return (DigestCredentials) this.withParameter(CNONCE.name(), QuotedString.from(cnonce));
  }

  public DigestCredentials withRealm(String realm) {
    return (DigestCredentials) this.withParameter(REALM.name(), QuotedString.from(realm));
  }

  public DigestCredentials withNonceCount(long nc) {
    return (DigestCredentials) this.withParameter(NONCE_COUNT.name(), Token.from(String.format("%08x", nc)));
  }

  public DigestCredentials withUsername(String user) {
    return (DigestCredentials) this.withParameter(USERNAME.name(), QuotedString.from(user));
  }

  public DigestCredentials withUri(String string) {
    return (DigestCredentials) this.withParameter(DIGEST_URI.name(), QuotedString.from(string));
  }

  public DigestCredentials withNonce(String string) {
    return (DigestCredentials) this.withParameter(NONCE.name(), QuotedString.from(string));
  }

  public DigestCredentials withOpaque(String string) {
    return (DigestCredentials) this.withParameter(OPAQUE.name(), QuotedString.from(string));
  }

  public DigestCredentials withQop(String string) {
    return (DigestCredentials) this.withParameter(QOP.name(), QuotedString.from(string));
  }

  public DigestCredentials withAlgorithm(String string) {
    return (DigestCredentials) this.withParameter(ALGORITHM.name(), Token.from(string));
  }

  public DigestCredentials withStale(boolean value) {
    return (DigestCredentials) this.withParameter(STALE.name(),
      Token.from(value ? "true"
                       : "false"));
  }

  public static DigestCredentials parseValue(String value) {
    if (!value.startsWith("Digest ")) {
      return null;
    }
    return new DigestCredentials(AuthorizationParser.INSTANCE.parseValue(value).parameters());
  }

}
