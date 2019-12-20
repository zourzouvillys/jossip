/**
 * 
 */
package io.rtcore.sip.message.message.api;

import java.io.IOException;

import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.SipResponse;

/**
 * 
 *
 */
public interface SipMessageVisitor {
  void visit(SipRequest msg) throws IOException;

  void visit(SipResponse msg) throws IOException;
}
