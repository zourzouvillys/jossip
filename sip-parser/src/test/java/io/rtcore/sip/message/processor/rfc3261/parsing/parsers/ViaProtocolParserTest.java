package io.rtcore.sip.message.processor.rfc3261.parsing.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.message.api.ViaProtocol;
import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.ViaProtocolParser;

public class ViaProtocolParserTest extends BaseParserTest<ViaProtocol> {

  public ViaProtocolParserTest() {
    super(new ViaProtocolParser());
  }

  @Test
  public void test() {

    assertEquals(ViaProtocol.of("SIP", "2.0", "UDP"), this.parse("SIP/2.0/UDP"));

  }

}
