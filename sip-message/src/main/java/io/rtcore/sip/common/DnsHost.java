package io.rtcore.sip.common;

import org.immutables.value.Value;

import com.google.common.net.InternetDomainName;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public interface DnsHost extends Host {

  /**
   * a normalized domain name. use Guavas' InternetDomainName normalization rules.
   */

  @Value.Parameter
  String domainName();

  @Value.Lazy
  @Override
  default String toUriString() {
    return this.domainName().toString();
  }

  static ImmutableDnsHost of(final String domain) {
    return ImmutableDnsHost.of(InternetDomainName.from(domain).toString());
  }

}
