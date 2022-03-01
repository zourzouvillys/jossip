package io.rtcore.sip.channels.netty.resolver;

import static org.reactivestreams.FlowAdapters.toSubscriber;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Objects;
import java.util.OptionalInt;

import com.google.auto.service.AutoService;

import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.resolver.DefaultHostsFileEntriesResolver;
import io.netty.resolver.dns.DefaultDnsCnameCache;
import io.netty.resolver.dns.DnsNameResolver;
import io.netty.resolver.dns.DnsNameResolverBuilder;
import io.netty.resolver.dns.UnixResolverDnsServerAddressStreamProvider;
import io.reactivex.rxjava3.core.Flowable;
import io.rtcore.sip.channels.SipNameResolver;
import io.rtcore.sip.channels.SipNameResolver.ResolutionResult;
import io.rtcore.sip.channels.netty.internal.NettySharedLoop;
import io.rtcore.sip.common.DnsHost;
import io.rtcore.sip.common.Host;
import io.rtcore.sip.common.IpHost;
import io.rtcore.sip.message.message.api.SipTransport;
import io.rtcore.sip.message.uri.SipUri;

@AutoService(SipNameResolver.Provider.class)
public final class NettySipNameResolverProvider implements SipNameResolver.Provider {

  // todo: needs to have lifecycle management
  private final DnsNameResolver resolver;

  /**
   *
   */

  public NettySipNameResolverProvider() throws IOException {
    this.resolver =
        new DnsNameResolverBuilder()
        .ttl(15, 300)
        .negativeTtl(30)
        .channelType(NioDatagramChannel.class)
        .eventLoop(NettySharedLoop.allocate().next())
        .cnameCache(new DefaultDnsCnameCache(15, 300))
        .completeOncePreferredResolved(true)
        .hostsFileEntriesResolver(new DefaultHostsFileEntriesResolver())
        .nameServerProvider(new UnixResolverDnsServerAddressStreamProvider(new File("/etc/resolv.conf")))
        .resolvedAddressTypes(DnsNameResolverBuilder.computeResolvedAddressTypes(InternetProtocolFamily.IPv4))
        .build();
  }

  /**
   *
   */

  @Override
  public SipNameResolver newNameResolver(final URI targetUri) {

    final String scheme = Objects.requireNonNull(targetUri.getScheme(), "scheme");

    //
    switch (scheme.toLowerCase()) {
      case "sip":
      case "sips":
        break;
      default:
        return null;
    }

    final SipUri uri = SipUri.parseString(targetUri.toString());

    final SipTransport transport = uri.transport().orElse(SipTransport.UDP);
    final Host host = uri.host();
    final OptionalInt port = uri.port();

    //
    if ((host instanceof final IpHost ip)) {
      final InetSocketAddress address = new InetSocketAddress(ip.inetAddress(), port.orElse(SipTransport.defaultPort(transport).orElse(5060)));
      final SipNameResolver.Address resultAddress = subscriber -> Flowable.just(address).subscribe(toSubscriber(subscriber));
      final ResolutionResult result = subscriber -> Flowable.just(resultAddress).subscribe(toSubscriber(subscriber));
      return new StaticNameResolver(result);
    }

    if (host instanceof final DnsHost dns) {
      final int portNumber = port.orElse(SipTransport.defaultPort(transport).orElse(5060));
      final InetSocketAddress address = new InetSocketAddress(dns.domainName(), portNumber);
      final SipNameResolver.Address resultAddress = subscriber -> Flowable.just(address).subscribe(toSubscriber(subscriber));
      final ResolutionResult result = subscriber -> Flowable.just(resultAddress).subscribe(toSubscriber(subscriber));
      return new StaticNameResolver(result);
    }

    return null;

  }

  @Override
  public String defaultScheme() {
    return "sip";
  }

  @Override
  public boolean isAvailable() {
    return true;
  }

  @Override
  public int priority() {
    return 5;
  }

}
