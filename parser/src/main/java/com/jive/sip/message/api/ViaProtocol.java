package com.jive.sip.message.api;

import com.google.common.base.Joiner;

public final class ViaProtocol {
  public static final ViaProtocol UDP = new ViaProtocol("SIP", "2.0", "UDP");
  public static final ViaProtocol TCP = new ViaProtocol("SIP", "2.0", "TCP");
  public static final ViaProtocol TLS = new ViaProtocol("SIP", "2.0", "TLS");
  public static final ViaProtocol SCTP = new ViaProtocol("SIP", "2.0", "SCTP");
  public static final ViaProtocol TLS_SCTP = new ViaProtocol("SIP", "2.0", "TLS-SCTP");
  public static final ViaProtocol WS = new ViaProtocol("SIP", "2.0", "WS");
  public static final ViaProtocol WSS = new ViaProtocol("SIP", "2.0", "WSS");
  public static final ViaProtocol DCCP = new ViaProtocol("SIP", "2.0", "DCCP");
  public static final ViaProtocol DTLS_DCCP = new ViaProtocol("SIP", "2.0", "DTLS-DCCP");
  public static final ViaProtocol DTLS_UDP = new ViaProtocol("SIP", "2.0", "DTLS-UDP");
  private final CharSequence name;
  private final CharSequence version;
  private final CharSequence transport;

  public static ViaProtocol forString(String protocol) {
    switch (protocol.toUpperCase()) {
    case "UDP": 
      return UDP;
    case "TCP": 
      return TCP;
    case "TLS": 
      return TLS;
    case "SCTP": 
      return SCTP;
    case "TLS-SCTP": 
      return TLS_SCTP;
    case "WS": 
      return WS;
    case "WSS": 
      return WSS;
    case "DCCP": 
      return DCCP;
    case "DTLS-DCCP": 
      return DTLS_DCCP;
    case "DTLS-UDP": 
      return DTLS_UDP;
    }
    return new ViaProtocol("SIP", "2.0", protocol.toUpperCase());
  }

  @Override
  public String toString() {
    return Joiner.on("/").join(name, version, transport);
  }

  public ViaProtocol(final CharSequence name, final CharSequence version, final CharSequence transport) {
    this.name = name;
    this.version = version;
    this.transport = transport;
  }

  public CharSequence name() {
    return this.name;
  }

  public CharSequence version() {
    return this.version;
  }

  public CharSequence transport() {
    return this.transport;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof ViaProtocol)) return false;
    final ViaProtocol other = (ViaProtocol) o;
    final Object this$name = this.name();
    final Object other$name = other.name();
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
    final Object this$version = this.version();
    final Object other$version = other.version();
    if (this$version == null ? other$version != null : !this$version.equals(other$version)) return false;
    final Object this$transport = this.transport();
    final Object other$transport = other.transport();
    if (this$transport == null ? other$transport != null : !this$transport.equals(other$transport)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $name = this.name();
    result = result * PRIME + ($name == null ? 43 : $name.hashCode());
    final Object $version = this.version();
    result = result * PRIME + ($version == null ? 43 : $version.hashCode());
    final Object $transport = this.transport();
    result = result * PRIME + ($transport == null ? 43 : $transport.hashCode());
    return result;
  }
}
