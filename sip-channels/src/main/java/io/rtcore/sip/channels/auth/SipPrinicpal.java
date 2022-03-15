package io.rtcore.sip.channels.auth;

import java.security.Principal;
import java.util.Map;

import com.google.common.base.MoreObjects;

public class SipPrinicpal implements Principal {

  private final String username;
  private final String realm;
  private final Map<String, ? extends Object> properties;

  public SipPrinicpal(String username, String realm, Map<String, ? extends Object> properties) {
    this.username = username;
    this.realm = realm;
    this.properties = properties;
  }

  /**
   * returns the contents of this principal in the form realm:username.
   */

  @Override
  public String getName() {
    return String.format("%s:%s", this.realm, this.username);
  }

  public String getRealm() {
    return this.realm;
  }

  public String getUsername() {
    return this.username;
  }

  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("username", username)
      .add("realm", realm)
      .add("properties", properties)
      .toString();
  }

}
