package com.jive.sip.stack.resolver;

import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.jive.sip.uri.api.SipUri;

/**
 * TODO: ensure we order priorities correctly
 * 
 * @author theo
 * 
 */

@Slf4j
public class DefaultLocationLookupService implements ILocationLookupService
{

  private IDnsResolver resolver;

  public void setResolver(final IDnsResolver resolver)
  {
    this.resolver = resolver;
  }

  @Override
  public List<ServiceResult> lookup(final SipUri input, final TransportProtocol[] services)
  {

    final HostAndPort hostp = input.getHost();
    Integer port;
    try
    {
      port = hostp.getPort();
    }
    catch (IllegalStateException e)
    {
      port = null;
    }

    final List<ServiceResult> result = Lists.newLinkedList();

    final TransportProtocol transport = this.getTransport(input, services);

    final String host = input.getHost().getHost();

    // if it's an IP, use that immediatly.

    if (InetAddresses.isInetAddress(host))
    {

      final InetAddress addr = InetAddresses.forString(host);

      if (port == null)
      {
        port = transport.getDefaultPort();
      }

      result.add(new DefaultServiceResult(transport, addr, port));

      return result;

    }

    // if the port is specified, then just lookup the host itself.

    if (port != null)
    {

      final List<InetAddress> addrs = this.resolver.getEntries(host);

      for (final InetAddress addr : addrs)
      {
        result.add(new DefaultServiceResult(this.getTransport(input, services), addr, port));
      }

      return result;

    }

    final List<IServiceRecord> srvs = this.resolver.getServiceRecords(input.isSecure() ? "sips" : "sip", transport.getType().toString(), host);

    if (srvs != null)
    {

      Collections.sort(srvs, new ServiceRecordComparator());

      for (final IServiceRecord srv : srvs)
      {

        if (InetAddresses.isInetAddress(srv.getTarget()))
        {
          result.add(new DefaultServiceResult(transport, InetAddresses.forString(srv.getTarget()), srv.getPort()));
        }
        else
        {

          final List<InetAddress> addrs = this.resolver.getEntries(srv.getTarget());

          for (final InetAddress addr : addrs)
          {
            result.add(new DefaultServiceResult(transport, addr, srv.getPort()));
          }

        }

      }

      return result;

    }

    final List<InetAddress> addrs = this.resolver.getEntries(host);

    if (addrs != null)
    {

      for (final InetAddress addr : addrs)
      {
        result.add(new DefaultServiceResult(this.getTransport(input, services), addr, transport.getDefaultPort()));
      }

    }

    return result;

  }

  /**
   * 
   * @param input
   * @param services
   * @return
   */

  private TransportProtocol getTransport(final SipUri input, final TransportProtocol[] services)
  {

    if (InetAddresses.isInetAddress(input.getHost().getHost()) || !input.getHost().hasPort())
    {

      if (input.isSecure())
      {
        return ILocationLookupService.TLS;
      }

      return ILocationLookupService.UDP;

    }

    // perform NAPTR lookup

    final List<INamingAuthorityPointer> naptrs = this.resolver.getNamingAuthorities(input.getHost().getHost());

    if (naptrs != null)
    {

      Collections.sort(naptrs, new NaptrPreferenceComparator());

      for (final TransportProtocol protocol : services)
      {

        if ((input.isSecure() == true) && (protocol.isSecure() == false))
        {
          continue;
        }

        log.debug("We support {}", protocol.getNamingPointer());

        for (final INamingAuthorityPointer ptr : naptrs)
        {

          log.debug("They support {}", ptr.getService());

          if (ptr.getService().equalsIgnoreCase(protocol.getNamingPointer()))
          {
            return protocol;
          }

        }

      }

      // hmm, none matching?

      throw new RuntimeException("Remote host supports no transports we do!");

    }

    // none found, try SRV:

    for (final TransportProtocol protocol : services)
    {

      final List<IServiceRecord> records = this.resolver.getServiceRecords(input.isSecure() ? "sips" : "sip", protocol.getType().toString(), input
          .getHost()
          .getHost());

      if (records != null)
      {
        return protocol;
      }

    }

    if (input.isSecure())
    {

      return ILocationLookupService.TLS;

    }

    return ILocationLookupService.UDP;

  }

}
