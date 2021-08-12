package io.rtcore.sip.message.auth;

// algorithm = "algorithm" EQUAL ( "MD5" / "MD5-sess" / "SHA-256" / "SHA-256-sess" / "SHA-512-256" /
// "SHA-512-256-sess" / token )

public enum StdDigestAlgo implements DigestAlgo {

  MD5,
  SHA_256,
  SHA_512_256,

  MD5_sess,
  SHA_256_sess,
  SHA_512_256_sess,
  ;

  private final String id;

  private StdDigestAlgo() {
    this.id = name().replace('_', '-');
  }

  @Override
  public String algId() {
    return this.id;
  }

}
