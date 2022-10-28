package io.rtcore.resolver.dns;

import java.util.Arrays;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.net.InternetDomainName;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
@JsonSerialize
@JsonDeserialize(builder = ImmutableNaptrValue.Builder.class)
public interface NaptrValue {

  @Value.Parameter
  int order();

  @Value.Parameter
  int preference();

  @Value.Parameter
  String flags();

  @Value.Parameter
  String service();

  @Value.Parameter
  String regexp();

  @Value.Parameter
  Optional<String> replacement();

  // priority weight port target.
  public static NaptrValue parse(String input) {

    System.err.println(input);
    String[] parts = input.split("[ ]+", 6);

    if (parts.length != 6) {
      throw new IllegalArgumentException();
    }

    System.err.println(Arrays.toString(parts));

    return ImmutableNaptrValue.of(
      Integer.parseUnsignedInt(parts[0]),
      Integer.parseUnsignedInt(parts[1]),
      dequote(parts[2]),
      dequote(parts[3]),
      dequote(parts[4]),
      Optional.of(parts[5]).filter(e -> !e.equals(".")).map(value -> InternetDomainName.from(value).toString()));

  }

  static String dequote(String value) {
    if (value.startsWith("\"") && value.endsWith("\"")) {
      return value.substring(1, value.length() - 2);
    }
    return value;
  }

}
