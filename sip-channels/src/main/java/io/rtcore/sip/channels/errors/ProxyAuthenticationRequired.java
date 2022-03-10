package io.rtcore.sip.channels.errors;

import java.util.List;
import java.util.stream.Collectors;

import io.rtcore.sip.channels.api.SipResponseFrame;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.message.message.SipResponse;

public class ProxyAuthenticationRequired extends ClientFailure {

  private static final long serialVersionUID = 1L;
  private final List<String> creds;

  public ProxyAuthenticationRequired(SipResponse res) {
    super(res);
    this.creds =
      res
        .headerValues(StandardSipHeaders.PROXY_AUTHENTICATE)
        .collect(Collectors.toUnmodifiableList());
  }

  public ProxyAuthenticationRequired(SipResponseFrame res) {
    super(res);
    this.creds =
      res.headerLines()
        .stream()
        .filter(e -> e.headerId() == StandardSipHeaders.PROXY_AUTHENTICATE)
        .map(e -> e.headerValues())
        .collect(Collectors.toUnmodifiableList());
  }

  public List<String> credentials() {
    return this.creds;
  }

  public String toString() {
    return super.toString() + ": " + creds;
  }

}
