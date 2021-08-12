package io.rtcore.sip.message.message.api.headers;

/**
 *
 */
public enum URIProtocols {
  SIP,
  SIPS,
  MAIL,
  OTHER;

  public static URIProtocols getType(String type) {
    type = type.toLowerCase().trim();
    if ("sip".equals(type)) {
      return SIP;
    }
    else if ("sips".equals(type)) {
      return SIPS;
    }
    else if ("mailto".equals(type)) {
      return MAIL;
    }
    else {
      return OTHER;
    }
  }
}
