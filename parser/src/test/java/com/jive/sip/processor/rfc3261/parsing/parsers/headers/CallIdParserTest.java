/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jive.sip.message.api.headers.CallId;
import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.processor.rfc3261.parsing.SipMessageParseFailureException;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class CallIdParserTest extends BaseParserTest<CallId>
{

  public CallIdParserTest()
  {
    super(new CallIdParser());
  }

  @Test
  public void testWithHost() throws SipMessageParseFailureException
  {
    assertEquals(new CallId("642783a7-d93bab97@172.20.103.51"),
        this.parse("642783a7-d93bab97@172.20.103.51"));
  }

  @Test
  public void testWithoutHost() throws SipMessageParseFailureException
  {
    assertEquals(new CallId("642783a7-d93bab97"), this.parse("642783a7-d93bab97"));
  }

  @Test
  public void testAtWithoutHost() throws SipMessageParseFailureException
  {
    assertEquals(null, this.parse("642783a7-d93bab97@", 18));
  }

  @Test
  public void testUsernameNotToken() throws SipMessageParseFailureException
  {
    assertEquals(new CallId("642783"), this.parse("642783$a7-d93bab97@172.20.103.51", 26));
  }

  @Test
  public void testHostNotToken() throws SipMessageParseFailureException
  {
    assertEquals(new CallId("642783a7-d93bab97@172.20.10"), this.parse("642783a7-d93bab97@172.20.10$3.51", 5));
  }
}
