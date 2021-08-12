/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.message.api.headers.CallId;
import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.processor.rfc3261.parsing.SipMessageParseFailureException;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.CallIdParser;

/**
 * 
 * 
 */
public class CallIdParserTest extends BaseParserTest<CallId> {

  public CallIdParserTest() {
    super(new CallIdParser());
  }

  @Test
  public void testWithHost() throws SipMessageParseFailureException {
    assertEquals(new CallId("642783a7-d93bab97@172.20.103.51"),
      this.parse("642783a7-d93bab97@172.20.103.51"));
  }

  @Test
  public void testWithoutHost() throws SipMessageParseFailureException {
    assertEquals(new CallId("642783a7-d93bab97"), this.parse("642783a7-d93bab97"));
  }

  @Test
  public void testAtWithoutHost() throws SipMessageParseFailureException {
    assertEquals(null, this.parse("642783a7-d93bab97@", 18));
  }

  @Test
  public void testUsernameNotToken() throws SipMessageParseFailureException {
    assertEquals(new CallId("642783"), this.parse("642783$a7-d93bab97@172.20.103.51", 26));
  }

  @Test
  public void testHostNotToken() throws SipMessageParseFailureException {
    assertEquals(new CallId("642783a7-d93bab97@172.20.10"), this.parse("642783a7-d93bab97@172.20.10$3.51", 5));
  }
}
