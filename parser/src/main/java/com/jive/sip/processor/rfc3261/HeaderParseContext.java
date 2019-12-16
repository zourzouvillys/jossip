package com.jive.sip.processor.rfc3261;

import java.util.List;

/**
 * Because a field need to be entirely accessible to a parser but may not encompass the whole header
 * field, an instance of this class keeps a pointer to the current position. The parser is
 * responsible for positioning the HeaderParseContext in the right place (e,g end of field) based on
 * the parse.
 * 
 * @author theo
 * 
 */

public interface HeaderParseContext extends CharSequence {

  String getName();

  List<String> getValue();

  String getSingleValue();

  /**
   * Returns the next byte in the parser context.
   * 
   * @return
   */

  byte peek();

  /**
   * @return the current position in the header parser context.
   */

  int position();

  /**
   * @param offset
   *          the new offset of the context reader index
   * @return the previous position
   */

  int position(final int offset);

  /**
   * Step over the given number of bytes
   * 
   * @throws Exception
   *           if you go over the end of the string.
   * 
   * @param bytes
   *          The number of bytes to skip over.
   */

  void consume(final int bytes);

  /**
   * copies the bytes from the current position
   * 
   * @param target
   * @param position
   * @param len
   */

  void get(final byte[] target, final int position, final int len);

  void get(final byte[] data);

  SipMessageManager getManager();

}
