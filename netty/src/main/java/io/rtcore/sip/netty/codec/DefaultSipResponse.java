package io.rtcore.sip.netty.codec;


public class DefaultSipResponse extends DefaultSipMessage implements SipResponse {

  private SipResponseStatus status;

  public DefaultSipResponse(SipVersion protocolVersion, SipResponseStatus status) {
    super(protocolVersion);
    this.status = status;
  }

  @Override
  public SipResponseStatus status() {
    return this.status;
  }

  @Override
  public String toString() {
    return SipMessageUtil.appendResponse(new StringBuilder(256), this).toString();
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = (31 * result) + status.hashCode();
    result = (31 * result) + super.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof DefaultSipResponse)) {
      return false;
    }
    DefaultSipResponse other = (DefaultSipResponse) o;
    return status.equals(other.status()) && super.equals(o);
  }

}
