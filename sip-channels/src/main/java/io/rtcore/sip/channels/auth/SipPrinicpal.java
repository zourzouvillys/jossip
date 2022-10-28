package io.rtcore.sip.channels.auth;

import java.security.Principal;

import com.google.common.base.MoreObjects;

import io.rtcore.sip.channels.api.SipAttributes;

public class SipPrinicpal implements Principal {

  private final String username;
  private final String realm;
  private final SipAttributes attributes;

  public SipPrinicpal(String username, String realm, SipAttributes attributes) {
    this.username = username;
    this.realm = realm;
    this.attributes = attributes;
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
      .add("attributes", attributes)
      .toString();
  }

}
