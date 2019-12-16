package com.jive.sip.processor.rfc3261.parsing.parsers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jive.sip.message.api.ViaProtocol;
import com.jive.sip.parsers.core.BaseParserTest;

public class ViaProtocolParserTest extends BaseParserTest<ViaProtocol> {

  public ViaProtocolParserTest() {
    super(new ViaProtocolParser());
  }

  @Test
  public void test() {

    assertEquals(new ViaProtocol("SIP", "2.0", "UDP"), this.parse("SIP/2.0/UDP"));

  }

}
