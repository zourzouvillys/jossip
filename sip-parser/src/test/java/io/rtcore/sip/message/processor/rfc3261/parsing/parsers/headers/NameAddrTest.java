/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.NameAddrParser;

/**
 * 
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
