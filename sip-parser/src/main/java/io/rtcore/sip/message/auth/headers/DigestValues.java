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

import java.util.Optional;

import org.immutables.value.Value;

import io.rtcore.sip.message.auth.DigestAlgo;
import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;

@Value.Immutable()
@Value.Style(
    jdkOnly = true,
    allowedClasspathAnnotations = { Override.class },
    defaultAsDefault = true)
public interface DigestValues {

  Optional<String> realm();

  Optional<String> domain();

  Optional<String> nonce();

  Optional<String> opaque();

  default boolean stale() {
    return false;
  }

  Optional<DigestAlgo> algorithm();

  Optional<String> username();

  Optional<String> uri();

  Optional<String> response();

  Optional<String> cnonce();

  Optional<String> qop();

  Optional<Integer> nonceCount();

  default DigestCredentials asCredentials() {

    Parameters params = DefaultParameters.EMPTY;

    if (this.algorithm().isPresent()) {
      params = params.withParameter(ALGORITHM, this.algorithm().get().algId());
    }

    if (this.realm().isPresent()) {
      params = params.withParameter(REALM, this.realm().get());
    }

    if (this.response().isPresent()) {
      params = params.withParameter(RESPONSE, this.response().get());
    }

    if (this.username().isPresent()) {
      params = params.withParameter(USERNAME, this.username().get());
    }

    if (this.domain().isPresent()) {
      params = params.withParameter(DOMAIN, this.domain().get());
    }

    if (this.cnonce().isPresent()) {
      params = params.withParameter(CNONCE, this.cnonce().get());
    }

    if (this.uri().isPresent()) {
      params = params.withParameter(DIGEST_URI, this.uri().get());
    }

    if (this.nonce().isPresent()) {
      params = params.withParameter(NONCE, this.nonce().get());
    }

    if (this.opaque().isPresent()) {
      params = params.withParameter(OPAQUE, this.opaque().get());
    }

    if (this.stale()) {
      params =
        params.withParameter(STALE,
          this.stale() ? Token.TRUE
                       : Token.FALSE);
    }

    if (this.qop().isPresent()) {
      params = params.withParameter(QOP, this.qop().get());
    }

    if (this.nonceCount().isPresent()) {
      params = params.withParameter(NONCE_COUNT, String.format("%08d", this.nonceCount().get()));
    }

    return new DigestCredentials(params);

  }

  public class Builder extends ImmutableDigestValues.Builder {
  }

  static Builder builder() {
    return new Builder();
  }

}
