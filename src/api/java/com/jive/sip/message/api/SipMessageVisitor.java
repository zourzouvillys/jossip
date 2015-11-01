/**
 * 
 */
package com.jive.sip.message.api;

import java.io.IOException;



/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */
public interface SipMessageVisitor
{
  void visit(SipRequest msg) throws IOException;
  void visit(SipResponse msg) throws IOException;
}
