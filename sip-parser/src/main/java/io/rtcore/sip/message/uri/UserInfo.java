/**
 * 
 */
package io.rtcore.sip.message.uri;

import java.util.Optional;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

/**
 */

@Value.Immutable(builder = false)
@Value.Style(
    jdkOnly = true,
    allowedClasspathAnnotations = { Override.class },
    visibility = ImplementationVisibility.PACKAGE,
    overshadowImplementation = true)
public abstract class UserInfo {

  @Value.Parameter
  public abstract String user();

  @Value.Parameter
  public abstract Optional<String> password();

  @Override
  public String toString() {
    if (password().isPresent()) {
      return user() + ":" + password().get();
    }
    return user();
  }

  public static UserInfo of(String user, String password) {
    return ImmutableUserInfo.of(user, Optional.ofNullable(password));
  }

  public static UserInfo of(String user) {
    return ImmutableUserInfo.of(user, Optional.empty());
  }

}
