package com.jive.sip.transport.tcp;

public interface TcpConnectionFactory
{

  TcpTransportListener create(final TcpChannel channel);

}
