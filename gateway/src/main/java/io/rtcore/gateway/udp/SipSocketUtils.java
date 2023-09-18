package io.rtcore.gateway.udp;

import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import io.netty.util.internal.SocketUtils;

public class SipSocketUtils {

  public static InetAddress defaultLocalAddress() {
    return SocketUtils.addressesFromNetworkInterface(networkInterfaces().iterator().next()).nextElement();
  }

  public static Collection<NetworkInterface> networkInterfaces() {
    final List<NetworkInterface> networkInterfaces = new ArrayList<>();
    try {
      final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      if (interfaces != null) {
        while (interfaces.hasMoreElements()) {
          networkInterfaces.add(interfaces.nextElement());
        }
      }
    }
    catch (final SocketException e) {
      throw new UncheckedIOException(e);
    }
    return Collections.unmodifiableList(networkInterfaces);
  }

}
