package io.rtcore.gateway.udp;

import java.io.UncheckedIOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import io.netty.util.internal.SocketUtils;

public final class SipSocketUtils {

  /**
   * if no address is specified, attempts to guess the best interface to bind to.
   * this is not really ideal, and should instead define explicity.
   */

  public static Optional<InetAddress> getDefaultAddress() {
    for (NetworkInterface iface : networkInterfaces()) {
      try {
        if (!iface.isUp()) {
          continue;
        }
        if (iface.isLoopback()) {
          continue;
        }
        if (!iface.isUp()) {
          continue;
        }
        for (InetAddress addr : Collections.list(iface.getInetAddresses())) {
          if (reasonableAddress(addr)) {
            return Optional.of(addr);
          }
        }
      } catch (SocketException ex) {
        throw new UncheckedIOException(ex);
      }
    }
    return Optional.empty();
  }

  private static boolean reasonableAddress(InetAddress addr) {
    if (addr instanceof Inet4Address) {
      return !addr.isMulticastAddress() || !addr.isAnyLocalAddress();
    }
    if (addr instanceof Inet6Address) {
      if (addr.isLinkLocalAddress()) {
        return false;
      }
      if (addr.isMulticastAddress()) {
        return false;
      }
      if (addr.isLoopbackAddress()) {
        return false;
      }
      return true;
    }
    return false;
  }

  /**
   * read-only list of the network interfaces on this machine.
   */

  public static List<NetworkInterface> networkInterfaces() {
    final List<NetworkInterface> networkInterfaces = new ArrayList<>();
    try {
      final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      if (interfaces != null) {
        while (interfaces.hasMoreElements()) {
          networkInterfaces.add(interfaces.nextElement());
        }
      }
    } catch (final SocketException e) {
      throw new UncheckedIOException(e);
    }
    return Collections.unmodifiableList(networkInterfaces);
  }

  /**
   * ip addresses on a specific interface.
   */

  public static List<InetAddress> addressesFromNetworkInterface(final NetworkInterface iface) {
    Enumeration<InetAddress> e = SocketUtils.addressesFromNetworkInterface(iface);
    // return as a List
    List<InetAddress> list = new ArrayList<>();
    while (e.hasMoreElements()) {
      list.add(e.nextElement());
    }
    return Collections.unmodifiableList(list);
  }

}
