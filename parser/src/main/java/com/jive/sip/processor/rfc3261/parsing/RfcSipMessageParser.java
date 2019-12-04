package com.jive.sip.processor.rfc3261.parsing;

import java.nio.ByteBuffer;

import com.jive.sip.base.api.RawMessage;


/**
 * Interface to convert a bunch of bytes into a {@link RawMessage}.
 *
 * @author theo
 *
 */

public interface RfcSipMessageParser
{

  /**
   * Performs a single-shot parse, without doing any validation at all.
   *
   * This method doesn't check the body length we received matches the length in the header, or anything else. It purely
   * ensures it's syntactically correct, and parses the fields into their relevant places.
   *
   * @param data
   *          Bytes of data, must by a syntactically valid message.
   * @return RawMessage
   * @throws SipMessageParseFailureException
   *           if data fails to parse.
   */


  RawMessage parse(final ByteBuffer buf);

  /**
   *
   */

  RawMessage parse(final byte[] data, final int length);

}
