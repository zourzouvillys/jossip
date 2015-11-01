package com.jive.sip.stack.resolver;

import java.net.InetAddress;

public interface ServiceResult
{

  TransportProtocol getTransport();

  InetAddress getAddress();

  int getPort();

}
