/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import org.junit.Test;

import com.jive.sip.message.api.NameAddr;
import com.jive.sip.parsers.core.BaseParserTest;

/**
 * @author Jeff Hutchins {@code <jhutchins@getjive.com>}
 *
 */
public class NameAddrTest extends BaseParserTest<NameAddr> {

  public NameAddrTest() {
    super(NameAddrParser.INSTANCE);
  }

  @Test
  public void test()

  {
    this.parse("sip:01341f4158ed2f6d9d000100620001@10.50.10.135:51620;transport=udp;srcadr=10.50.10.69:5060");
  }
}
