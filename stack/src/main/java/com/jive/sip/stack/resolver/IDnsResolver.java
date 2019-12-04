package com.jive.sip.stack.resolver;

import java.net.InetAddress;
import java.util.List;

public interface IDnsResolver
{
  /**
   * Performs a lookup for the naming authority records (NAPTR).
   * 
   * @param domain
   *          The domain to query
   * 
   * @return An unordered and unfiltered list of naming authorities, or null if the host/type are not found.
   * 
   */

  public List<INamingAuthorityPointer> getNamingAuthorities(final String domain);

  /**
   * Performs an SRV lookup for _<service>._<protocol>.<name>.
   * 
   * @param service
   *          The service to lookup, e.g "sip"
   * 
   * @param protocol
   *          The protocol to lookup, e.g "udp"
   * 
   * @param name
   *          The hostname to lookup, e.g provider.com
   * 
   * @return An unordered list of {@link IServiceRecord} instances, or null if the host/type are not found.
   */

  public List<IServiceRecord> getServiceRecords(final String service, final String protocol, final String name);

  /**
   * Performs A & AAAA record lookup for the given hostname and returns the results.
   * 
   * @param hostname
   *          The hostname to look up.
   * 
   * @return An unordered list of A and AAAA records, or null if any A or AAAA records are not found.
   * 
   *         TODO: add A or AAAA only flag.
   * 
   */

  public List<InetAddress> getEntries(final String hostname);

}
