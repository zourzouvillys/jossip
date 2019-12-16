/**
 * 
 */
package com.jive.sip.processor.uri.parsers;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.uri.api.UrnService;
import com.jive.sip.uri.api.UrnUri;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
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
