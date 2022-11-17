package io.rtcore.sip.channels.netty.tcp;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProtocols;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class SipTlsUtils {

  public static SslContext createClient() {
    try {
      return SslContextBuilder
        .forClient()
        .trustManager(InsecureTrustManagerFactory.INSTANCE)
        // .trustManager(FingerprintTrustManagerFactory.builder("SHA1").fingerprints(...).build())
        // .ciphers(List.of("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "AES256-SHA256"))
        .protocols(SslProtocols.TLS_v1_3, SslProtocols.TLS_v1_2, SslProtocols.TLS_v1_1, SslProtocols.TLS_v1)
        .build();
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static SslContext createClient(KeyManagerFactory keyManagerFactory) {
    try {
      return SslContextBuilder
        .forClient()
        .trustManager(InsecureTrustManagerFactory.INSTANCE)
        .keyManager(keyManagerFactory)
        // .trustManager(FingerprintTrustManagerFactory.builder("SHA1").fingerprints(...).build())
        // .ciphers(List.of("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "AES256-SHA256"))
        .protocols("TLSv1.3", "TLSv1.2")
        .build();
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static SslContext createClient(File key, File certs) {
    try {
      return SslContextBuilder
        .forClient()
        .trustManager(InsecureTrustManagerFactory.INSTANCE)
        .keyManager(certs, key)
        // .trustManager(FingerprintTrustManagerFactory.builder("SHA1").fingerprints(...).build())
        // .ciphers(List.of("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "AES256-SHA256"))
        .protocols("TLSv1.3", "TLSv1.2")
        .build();
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static SslContext createClient(KeyManagerFactory keyManagerFactory) {
    try {
      return SslContextBuilder
        .forClient()
        .trustManager(InsecureTrustManagerFactory.INSTANCE)
        .keyManager(keyManagerFactory)
        // .trustManager(FingerprintTrustManagerFactory.builder("SHA1").fingerprints(...).build())
        // .ciphers(List.of("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "AES256-SHA256"))
        .protocols("TLSv1.3", "TLSv1.2")
        .build();
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static SslContext createClient(File key, File certs) {
    try {
      return SslContextBuilder
        .forClient()
        .trustManager(InsecureTrustManagerFactory.INSTANCE)
        .keyManager(certs, key)
        // .trustManager(FingerprintTrustManagerFactory.builder("SHA1").fingerprints(...).build())
        // .ciphers(List.of("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "AES256-SHA256"))
        .protocols("TLSv1.3", "TLSv1.2")
        .build();
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static SslContext createClient(PrivateKey key, X509Certificate... certs) {
    try {
      return SslContextBuilder
        .forClient()
        .trustManager(InsecureTrustManagerFactory.INSTANCE)
        .keyManager(key, certs)
        // .trustManager(FingerprintTrustManagerFactory.builder("SHA1").fingerprints(...).build())
        // .ciphers(List.of("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "AES256-SHA256"))
        .protocols("TLSv1.3", "TLSv1.2")
        .build();
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static SslContext createServer(PrivateKey key, X509Certificate... certs) {
    try {
      return SslContextBuilder.forServer(key, List.of(certs))
        .trustManager(InsecureTrustManagerFactory.INSTANCE)
        .protocols("TLSv1.3", "TLSv1.2")
        .build();
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
