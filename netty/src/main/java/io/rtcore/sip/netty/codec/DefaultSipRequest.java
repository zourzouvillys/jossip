package io.rtcore.sip.netty.codec;


public class DefaultSipRequest extends DefaultSipMessage implements SipRequest {

  private static final int HASH_CODE_PRIME = 31;

  private final SipMethod method;
  private final String ruri;

  public DefaultSipRequest(SipVersion protocolVersion, SipMethod method, String ruri, boolean validateHeaders) {
    super(protocolVersion);
    this.method = method;
    this.ruri = ruri;
  }

  @Override
  public String uri() {
    return this.ruri;
  }

  @Override
  public SipMethod method() {
    return this.method;
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME * result) + method.hashCode();
    result = (HASH_CODE_PRIME * result) + ruri.hashCode();
    result = (HASH_CODE_PRIME * result) + super.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof DefaultSipRequest)) {
      return false;
    }

    DefaultSipRequest other = (DefaultSipRequest) o;

    return method().equals(other.method())
      &&
      uri().equalsIgnoreCase(other.uri())
      &&
      super.equals(o);
  }

  @Override
  public String toString() {
    return SipMessageUtil.appendRequest(new StringBuilder(256), this).toString();
  }

}
