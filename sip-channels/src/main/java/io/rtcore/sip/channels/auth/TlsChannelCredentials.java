package io.rtcore.sip.channels.auth;

public final class TlsChannelCredentials implements SipChannelCredentials {

  /**
   *
   */

  // private final byte[] certificateChain;
  // private final byte[] privateKey;
  // private final String privateKeyPassword;

  // private final List<KeyManager> keyManagers;
  // private final byte[] rootCertificates;
  // private final List<TrustManager> trustManagers;

  public static TlsChannelCredentials create() {
    return new TlsChannelCredentials();
  }

}
