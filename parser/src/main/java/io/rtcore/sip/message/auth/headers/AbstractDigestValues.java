package io.rtcore.sip.message.auth.headers;

import static io.rtcore.sip.message.auth.headers.DigestCredentials.ALGORITHM;
import static io.rtcore.sip.message.auth.headers.DigestCredentials.CNONCE;
import static io.rtcore.sip.message.auth.headers.DigestCredentials.DIGEST_URI;
import static io.rtcore.sip.message.auth.headers.DigestCredentials.DOMAIN;
import static io.rtcore.sip.message.auth.headers.DigestCredentials.NONCE;
import static io.rtcore.sip.message.auth.headers.DigestCredentials.NONCE_COUNT;
import static io.rtcore.sip.message.auth.headers.DigestCredentials.OPAQUE;
import static io.rtcore.sip.message.auth.headers.DigestCredentials.QOP;
import static io.rtcore.sip.message.auth.headers.DigestCredentials.REALM;
import static io.rtcore.sip.message.auth.headers.DigestCredentials.RESPONSE;
import static io.rtcore.sip.message.auth.headers.DigestCredentials.STALE;
import static io.rtcore.sip.message.auth.headers.DigestCredentials.USERNAME;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;

@Value.Immutable()
@Value.Style(typeImmutable = "*", defaultAsDefault = true)
public interface AbstractDigestValues {

  @Nullable
  String realm();

  @Nullable
  String domain();

  @Nullable
  String nonce();

  @Nullable
  String opaque();

  default boolean stale() {
    return false;
  }

  @Nullable
  String algorithm();

  @Nullable
  String username();

  @Nullable
  String uri();

  @Nullable
  String response();

  @Nullable
  String cnonce();

  @Nullable
  String qop();

  @Nullable
  Integer nonceCount();

  default DigestCredentials asCredentials() {

    Parameters params = DefaultParameters.EMPTY;

    if (this.algorithm() != null) {
      params = params.withParameter(ALGORITHM, this.algorithm());
    }

    if (this.realm() != null) {
      params = params.withParameter(REALM, this.realm());
    }

    if (this.response() != null) {
      params = params.withParameter(RESPONSE, this.response());
    }

    if (this.username() != null) {
      params = params.withParameter(USERNAME, this.username());
    }

    if (this.domain() != null) {
      params = params.withParameter(DOMAIN, this.realm());
    }

    if (this.cnonce() != null) {
      params = params.withParameter(CNONCE, this.cnonce());
    }

    if (this.uri() != null) {
      params = params.withParameter(DIGEST_URI, this.uri());
    }

    if (this.nonce() != null) {
      params = params.withParameter(NONCE, this.nonce());
    }

    if (this.opaque() != null) {
      params = params.withParameter(OPAQUE, this.opaque());
    }

    params =
      params.withParameter(STALE,
        this.stale() ? Token.TRUE
                     : Token.FALSE);

    if (this.qop() != null) {
      params = params.withParameter(QOP, this.qop());
    }

    if (this.nonceCount() != null) {
      params = params.withParameter(NONCE_COUNT, String.format("%08d", this.nonceCount()));
    }

    return new DigestCredentials(params);

  }
}
