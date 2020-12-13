package io.rtcore.sip.proxy.transport.stream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.io.BaseEncoding;
import com.google.common.net.InternetDomainName;

@Value.Immutable
public interface TlsInfo {

  /**
   * 
   */

  @JsonProperty
  InetSocketAddress local();

  /**
   * 
   */

  @JsonProperty
  InetSocketAddress remote();

  /**
   * the server name indication (hostname only supported) we sent. note that sending of different
   * values for SNI may return different streams or certificates, so we should not reuse unless the
   * same SNI is used.
   */

  @JsonProperty
  Optional<InternetDomainName> serverHostName();

  /**
   * negotiated TLS protocol version: TLSv1, TLSv1.1, TLSv1.2.
   */

  @JsonProperty 
  String tlsProtocol();

  /**
   * Neogitated cipher suite.
   */

  @JsonProperty
  String cipherSuite();

  /**
   * Base64 encoded TLS session id.
   */

  @JsonProperty
  String sessionId();

  /**
   * the peer certificate chain provided to us, with index 0 being the actual certificate.
   */

  @JsonSerialize(contentUsing = CertSerializer.class)
  List<Certificate> peerCertificates();

  /**
   * serialize the encoded X509 cert using base64. don't add the prefix or suffix "---- BEGIN xyz
   * ----".
   */

  public static final class CertSerializer extends JsonSerializer<Certificate> {

    @Override
    public void serialize(Certificate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      try {
        gen.writeString(BaseEncoding.base64().encode(value.getEncoded()));
      }
      catch (CertificateEncodingException e) {
        throw new IOException(e);
      }
    }

  }

}
