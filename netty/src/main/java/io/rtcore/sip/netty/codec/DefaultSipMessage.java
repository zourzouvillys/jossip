package io.rtcore.sip.netty.codec;


public class DefaultSipMessage extends DefaultSipObject implements SipMessage {

  private final SipVersion protocolVersion;
  private final DefaultSipHeaders headers;

  protected DefaultSipMessage(SipVersion protocolVersion) {
    this.protocolVersion = protocolVersion;
    this.headers = new DefaultSipHeaders();
  }

  @Override
  public SipVersion protocolVersion() {
    return this.protocolVersion;
  }

  @Override
  public SipHeaders headers() {
    return this.headers;
  }

}
