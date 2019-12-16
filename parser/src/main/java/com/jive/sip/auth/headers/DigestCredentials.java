package com.jive.sip.auth.headers;

import java.util.Collection;

import com.jive.sip.base.api.Token;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.QuotedString;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.SipParameterDefinition;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parameters.tools.ParameterUtils;

import lombok.Builder;
import lombok.Singular;

public class DigestCredentials extends Authorization {

  @Builder
  public static final class DigestValues {

    private final String realm;
    private final String domain;
    private final String nonce;
    private final String opaque;
    private boolean stale = false;
    private final String algorithm;
    private final String username;
    private final String uri;
    private final String response;
    private final String cnonce;
    private final String qop;
    private final Integer nonceCount;

    @Singular
    private final Collection<RawParameter> params;

    public static class DigestValuesBuilder {

      public DigestCredentials build() {

        Parameters params = DefaultParameters.EMPTY;

        if (this.algorithm != null) {
          params = params.withParameter(ALGORITHM, this.algorithm);
        }

        if (this.realm != null) {
          params = params.withParameter(REALM, this.realm);
        }

        if (this.response != null) {
          params = params.withParameter(RESPONSE, this.response);
        }

        if (this.username != null) {
          params = params.withParameter(USERNAME, this.username);
        }

        if (this.domain != null) {
          params = params.withParameter(DOMAIN, this.realm);
        }

        if (this.cnonce != null) {
          params = params.withParameter(CNONCE, this.cnonce);
        }

        if (this.uri != null) {
          params = params.withParameter(DIGEST_URI, this.uri);
        }

        if (this.nonce != null) {
          params = params.withParameter(NONCE, this.nonce);
        }

        if (this.opaque != null) {
          params = params.withParameter(OPAQUE, this.opaque);
        }

        params =
          params.withParameter(STALE,
            this.stale ? Token.TRUE
                       : Token.FALSE);

        if (this.qop != null) {
          params = params.withParameter(QOP, this.qop);
        }

        if (this.nonceCount != null) {
          params = params.withParameter(NONCE_COUNT, String.format("%08d", this.nonceCount));
        }

        return new DigestCredentials(params);

      }

    }

  }

  public static DigestValues.DigestValuesBuilder builder() {
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
  protected static final SipParameterDefinition<Token> RESPONSE = ParameterUtils.createTokenParameterDefinition("response");
  protected static final SipParameterDefinition<String> REALM = ParameterUtils.createQuotedStringParameterDefinition("realm");
  protected static final SipParameterDefinition<String> DOMAIN = ParameterUtils.createQuotedStringParameterDefinition("domain");
  protected static final SipParameterDefinition<Token> STALE = ParameterUtils.createTokenParameterDefinition("stale");

  public static final String MD5 = "MD5";

  public DigestCredentials() {
    this(DefaultParameters.EMPTY);
  }

  public DigestCredentials(final Parameters parameters) {
    super("Digest", parameters);
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

  /**
   * null if not set.
   */

  public Long nonceCount() {
    return this.getParameter(NONCE_COUNT).map(e -> Long.parseLong(e.toString())).orElse(null);
  }

  /**
   * returns an instance with an incremented nonce count.
   */

  public DigestCredentials withIncrementedCnonceCount() {
    Token nc =
      Token.from(String.format("%08d",
        (this.nonceCount() == null) ? Token.from(1)
                                    : Token.from(this.nonceCount() + 1)));
    return (DigestCredentials) this.withParameter(NONCE_COUNT.getName(), nc);
  }

  public DigestCredentials withResponse(String authorization) {
    return (DigestCredentials) this.withParameter(RESPONSE.getName(), QuotedString.from(authorization)).withoutParameter(STALE.getName());
  }

  public DigestCredentials withCnonce(String cnonce) {
    return (DigestCredentials) this.withParameter(CNONCE.getName(), QuotedString.from(cnonce));
  }

  public DigestCredentials withNonceCount(long nc) {
    return (DigestCredentials) this.withParameter(NONCE_COUNT.getName(), Token.from(String.format("%08x", nc)));
  }

  public DigestCredentials withUsername(String user) {
    return (DigestCredentials) this.withParameter(USERNAME.getName(), QuotedString.from(user));
  }

  public DigestCredentials withUri(String string) {
    return (DigestCredentials) this.withParameter(DIGEST_URI.getName(), QuotedString.from(string));
  }

}
