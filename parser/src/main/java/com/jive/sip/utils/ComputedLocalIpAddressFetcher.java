package com.jive.sip.utils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ComputedLocalIpAddressFetcher implements IpAddressFetcher
{
  @Override
  public String getIpAddress()
  {
    String ip = null;
    String localIp = null;
    try
    {
      Enumeration<NetworkInterface> nie = NetworkInterface.getNetworkInterfaces();
      while (nie.hasMoreElements())
      {
        NetworkInterface ifc = nie.nextElement();
        if (ifc.isLoopback() || !ifc.isUp())
        {
          continue;
        }

        Enumeration<InetAddress> iae = ifc.getInetAddresses();
        while (iae.hasMoreElements())
        {
          InetAddress addr = iae.nextElement();
          if (addr instanceof Inet6Address)
          {
            continue;
          }
          else if (addr.getHostAddress().startsWith("192.168"))
          {
        	// Don't prefer these addresses - but we will use it if we don't find anything else
        	localIp = addr.getHostAddress();
            continue;
          }
          ip = addr.getHostAddress();
          break;
        }
      }
    }
    catch (SocketException e)
    {
      throw new RuntimeException(e);
    }
    if (null == ip && null != localIp)
    {
    	return localIp;
    }
    return ip;
  }
}
