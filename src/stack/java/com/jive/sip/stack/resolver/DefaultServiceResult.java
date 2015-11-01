package com.jive.sip.stack.resolver;

import java.net.InetAddress;

import lombok.Value;


@Value
public class DefaultServiceResult implements ServiceResult
{
  private final TransportProtocol transport;
  private final InetAddress address;
  private final int port;
}
