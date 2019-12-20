package io.rtcore.sip.message.processor.rfc3261.parsing;

import java.nio.ByteBuffer;

import io.rtcore.sip.message.message.SipMessage;

/**
 * Interface implemented by SIP messages parser implementations which takes raw bytes and converts
 * it into a SIP model message.
 * 
 * 
 * 
 */

public interface SipMessageParser {

  /**
   * 
   * @param data
   *          A single SIP message, without any leading or training bytes.
   * @return A new {@link SipMessage} instance.
   */

  SipMessage parse(final ByteBuffer data) throws SipMessageParseFailureException;

}
