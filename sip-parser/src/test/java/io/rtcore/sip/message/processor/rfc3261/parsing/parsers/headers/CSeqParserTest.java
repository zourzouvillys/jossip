/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.message.api.CSeq;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.processor.rfc3261.parsing.SipMessageParseFailureException;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.CSeqParser;

/**
 */

public class CSeqParserTest extends BaseParserTest<CSeq> {

  public CSeqParserTest() {
    super(new CSeqParser());
  }

  @Test
  public void testParsing() throws SipMessageParseFailureException {
    assertEquals(new CSeq(1, SipMethod.INVITE), this.parse("1 INVITE"));
    assertEquals(new CSeq(1, SipMethod.INVITE), this.parse("1      INVITE"));
    assertEquals(new CSeq(0, SipMethod.INVITE), this.parse("0 INVITE"));
    assertEquals(new CSeq(99, SipMethod.INVITE), this.parse("99  \t \t    INVITE"));
  }

}
