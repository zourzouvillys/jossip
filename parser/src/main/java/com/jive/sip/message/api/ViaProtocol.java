package com.jive.sip.message.api;

import com.google.common.base.Joiner;

import lombok.Value;

@Value
public class ViaProtocol {

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

  private CharSequence name;
  private CharSequence version;
  private CharSequence transport;

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

}
