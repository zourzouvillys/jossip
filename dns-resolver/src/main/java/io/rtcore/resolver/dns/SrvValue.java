package io.rtcore.resolver.dns;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.net.InternetDomainName;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
@JsonSerialize
@JsonDeserialize(builder = ImmutableSrvValue.Builder.class)
public interface SrvValue {

  @Value.Parameter
  int priority();

  @Value.Parameter
  int weight();

  @Value.Parameter
  int port();

  @Value.Parameter
  String target();

  // priority weight port target.
  public static SrvValue parse(String input) {

    String[] parts = input.split("[ ]+", 4);

    if (parts.length != 4) {
      throw new IllegalArgumentException();
    }

    return ImmutableSrvValue.of(
      Integer.parseUnsignedInt(parts[0]),
      Integer.parseUnsignedInt(parts[1]),
      Integer.parseUnsignedInt(parts[2]),
      InternetDomainName.from(parts[3]).toString());

  }

}
