/**
 * 
 */
package com.jive.sip.processor.rfc3261.auth;

import java.security.Principal;

import com.jive.sip.message.api.SipRequest;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public interface RequestAuthorizer
{
  /**
   * @return
   */
  public abstract boolean isPresent(SipRequest request);

  /**
   * @return
   */
  public abstract boolean isAuthenticated(SipRequest request);

  /**
   * @return
   */
  public abstract boolean isAuthorized(SipRequest request);

  /**
   * @return
   */
  public abstract Principal getPrincipal(SipRequest request);
}
