/**
 * 
 */
package com.jive.sip.uri.api;

import java.util.Optional;

import lombok.Value;
import lombok.experimental.Wither;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
@Value
@Wither
public class UserInfo {
  private final String user;
  private final Optional<String> password;

  public UserInfo(String user, Optional<String> password) {
    this.user = user;
    this.password = password;
  }

  public UserInfo(String user, String password) {
    this.user = user;
    this.password = Optional.of(password);
  }

  public UserInfo(String user) {
    this.user = user;
    this.password = Optional.empty();
  }

  public String toString() {
    if (password.isPresent()) {
      return user + ":" + password.get();
    }
    return user;
  }
}
