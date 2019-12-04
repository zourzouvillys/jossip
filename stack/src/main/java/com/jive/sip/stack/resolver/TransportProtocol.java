package com.jive.sip.stack.resolver;

import com.jive.sip.message.api.SipTransport;

public class TransportProtocol
{

  private final SipTransport type;
  private final String naptr;
  private final int port;
  private final boolean secure;

  public TransportProtocol(final SipTransport type, final String naptr, final int port, final boolean secure)
  {
    this.type = type;
    this.naptr = naptr;
    this.port = port;
    this.secure = secure;
  }

  public SipTransport getType()
  {
    return this.type;
  }

  public String getNamingPointer()
  {
    return this.naptr;
  }

  public int getDefaultPort()
  {
    return this.port;
  }

  public boolean isSecure()
  {
    return this.secure;
  }

  @Override
  public String toString()
  {
    return this.getType().toString();
  }

}
