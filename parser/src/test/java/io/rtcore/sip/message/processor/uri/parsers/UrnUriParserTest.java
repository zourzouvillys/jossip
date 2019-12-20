/**
 * 
 */
package io.rtcore.sip.message.processor.uri.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.processor.uri.parsers.UrnUriParser;
import io.rtcore.sip.message.uri.UrnService;
import io.rtcore.sip.message.uri.UrnUri;

/**
 * 
 * 
 */
public class UrnUriParserTest extends BaseParserTest<UrnUri> {

  public UrnUriParserTest() {
    super(UrnUriParser.SERVICE);
  }

  @Test
  public void test() {
    assertEquals(new UrnUri(UrnUri.SERVICE, new UrnService("sos")), this.parse("service:sos"));
    assertEquals(new UrnUri(UrnUri.SERVICE, new UrnService("sos.ambulance")), this.parse("service:sos.ambulance"));
    assertEquals(new UrnUri(UrnUri.SERVICE, new UrnService("sos.animal-control")),
      this.parse("service:sos.animal-control"));
  }

}
