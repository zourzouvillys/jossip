package com.jive.sip.stack.resolver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Cache;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.NAPTRRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.Type;

import com.google.common.collect.Lists;

@Slf4j
public class DefaultDnsResolver implements IDnsResolver
{

  private final Resolver resolver;
  private final Cache cache = new Cache();

  public DefaultDnsResolver()
  {
    try
    {
      this.resolver = new ExtendedResolver();
    }
    catch (final UnknownHostException e)
    {
      throw new RuntimeException(e);
    }
  }

  private Record[] lookup(final Lookup lookup)
  {

    lookup.setCache(this.cache);
    lookup.setResolver(this.resolver);
    lookup.run();

    if (lookup.getResult() == Lookup.SUCCESSFUL)
    {
      return lookup.run();
    }
    else if (lookup.getResult() == Lookup.HOST_NOT_FOUND || lookup.getResult() == Lookup.TYPE_NOT_FOUND)
    {
      return null;
    }
    else if (lookup.getResult() == Lookup.UNRECOVERABLE || lookup.getResult() == Lookup.TRY_AGAIN)
    {
      log.warn("DNS problem querying {}", lookup);
    }

    throw new RuntimeException("Lookup failed");

  }

  @Override
  public List<INamingAuthorityPointer> getNamingAuthorities(final String domain)
  {
    try
    {

      final Record[] records = this.lookup(new Lookup(domain, Type.NAPTR));

      if (records == null)
      {
        return null;
      }

      final List<INamingAuthorityPointer> naptrs = Lists.newLinkedList();

      for (final Record record : records)
      {

        final NAPTRRecord naptr = (NAPTRRecord) record;

        log.debug("NAPTR: {} ", naptr);

        naptrs.add(new INamingAuthorityPointer()
        {

          @Override
          public String getService()
          {
            return naptr.getService();
          }

          @Override
          public String getReplacement()
          {
            return naptr.getReplacement().toString();
          }

          @Override
          public String getRegularExpression()
          {
            return naptr.getRegexp();
          }

          @Override
          public int getPreferences()
          {
            return naptr.getPreference();
          }

          @Override
          public int getOrder()
          {
            return naptr.getOrder();
          }

          @Override
          public String getFlags()
          {
            return naptr.getFlags();
          }

        });

      }

      return naptrs;

    }
    catch (final Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<IServiceRecord> getServiceRecords(final String service, final String transport, final String name)
  {

    try
    {
      final Record[] records = this.lookup(new Lookup(String.format("_%s._%s.%s", service, transport, name), Type.SRV));

      if (records == null)
      {
        return null;
      }

      final List<IServiceRecord> srvs = Lists.newLinkedList();

      for (final Record record : records)
      {

        final SRVRecord srv = (SRVRecord) record;

        log.debug("SRV: {}", srv);

        srvs.add(new IServiceRecord()
        {

          @Override
          public int getWeight()
          {
            return srv.getWeight();
          }

          @Override
          public String getTarget()
          {
            return srv.getTarget().toString();
          }

          @Override
          public int getPriority()
          {
            return srv.getPriority();
          }

          @Override
          public int getPort()
          {
            return srv.getPort();
          }

        });

      }

      return srvs;

    }
    catch (final Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<InetAddress> getEntries(final String hostname)
  {
    try
    {

      final List<InetAddress> results = Lists.newLinkedList();

      boolean found = false;

      Record[] records = this.lookup(new Lookup(hostname, Type.A));

      if (records != null)
      {
        found = true;
        for (final Record record : records)
        {
          final ARecord a = (ARecord) record;
          log.debug("A: {}", a);
          results.add(a.getAddress());
        }
      }

      records = this.lookup(new Lookup(hostname, Type.AAAA));

      if (records != null)
      {
        found = true;
        for (final Record record : records)
        {
          final AAAARecord aaaa = (AAAARecord) record;
          log.debug("AAAA: {}", aaaa);
          results.add(aaaa.getAddress());

        }
      }

      if (found == false)
      {
        return null;
      }

      return results;

    }
    catch (final Exception e)
    {
      throw new RuntimeException(e);
    }

  }

}
